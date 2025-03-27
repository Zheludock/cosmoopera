package com.example.l21v3.ui.personal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import com.example.l21v3.model.data.EmployeeRepository
import com.example.l21v3.model.data.Rank
import com.example.l21v3.model.data.SquadRepository
import com.example.l21v3.ui.personal.military.Section
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseDepartmentViewModel(
    protected val employeeRepository: EmployeeRepository,
    protected val squadRepository: SquadRepository
) : ViewModel() {
    protected abstract val role: String
    protected abstract val department: String

    protected val _sections = MutableLiveData<List<Section>>()
    val sections: LiveData<List<Section>> = _sections

    protected val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    protected val _selectCommanderEvent = MutableLiveData<SelectCommanderEvent>()
    val selectCommanderEvent: LiveData<SelectCommanderEvent> = _selectCommanderEvent

    sealed class UiState {
        object Loading : UiState()
        data class Success(val sections: List<Section>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    protected fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val employees = employeeRepository.getEmployeesByRole(role)
                val squads = squadRepository.getSquadByDepartment(department)

                val sections = mutableListOf<Section>().apply {
                    add(Section(
                        id = "no_squad",
                        title = "Без отряда",
                        employees = employees.filter { it.currentSquadId == null }
                    ))

                    squads.forEach { squad ->
                        add(Section(
                            id = squad.id,
                            title = squad.name,
                            employees = employees.filter { it.currentSquadId == squad.id },
                            squad = squad,
                            maxSize = null
                        ))
                    }
                }

                _sections.postValue(sections)
                _uiState.value = UiState.Success(sections)
            } catch (e: Exception) {
                _toastMessage.value = "Ошибка загрузки данных"
                _uiState.value = UiState.Error("Ошибка загрузки данных")
            }
        }
    }

    open fun transferEmployee(employee: Employee, newSquadId: String?) {
        viewModelScope.launch {
            try {
                var updatedEmployee = employee.copy(currentSquadId = newSquadId)
                val oldSquadId = employee.currentSquadId

                // Если сотрудник был командиром
                if (employee.isCommander) {
                    // Снимаем флаг командира
                    updatedEmployee = updatedEmployee.copy(isCommander = false)

                    // Обновляем старый отряд
                    oldSquadId?.let { squadId ->
                        val oldSquad = squadRepository.getSquadById(squadId)
                        if (oldSquad?.commanderId == employee.id) {
                            squadRepository.updateSquadCommander(squadId, null)
                        }
                    }
                }

                // Сохраняем изменения
                employeeRepository.updateEmployee(updatedEmployee)
                loadData()

                _toastMessage.postValue(
                    if (oldSquadId != null) "${employee.name} переведен в новый отряд"
                    else "${employee.name} назначен в отряд"
                )

            } catch (e: Exception) {
                _toastMessage.postValue("Ошибка перевода сотрудника: ${e.localizedMessage}")
            }
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            employeeRepository.deleteEmployee(employee)
        }
    }

    fun assignCommander(squad: Squad, employee: Employee) {
        viewModelScope.launch {
            try {
                // Получаем текущий состав отряда
                val squadMembers = employeeRepository.getEmployeesBySquad(squad.id)

                // Определяем ранг кандидата
                val candidateRank = Rank.entries.find { it.displayName == employee.rank }
                    ?: throw IllegalArgumentException("Некорректное звание сотрудника")

                // Проверяем наличие более высоких званий
                val hasSuperiorRank = squadMembers.any { member ->
                    val memberRank = Rank.entries.find { it.displayName == member.rank }
                    (memberRank?.order ?: 0) > candidateRank.order
                }

                if (hasSuperiorRank) {
                    _toastMessage.postValue("В отряде есть сотрудники с более высоким званием!")
                    return@launch
                }

                // Снимаем предыдущего командира
                squad.commanderId?.let { oldCommanderId ->
                    employeeRepository.updateCommanderStatus(oldCommanderId, false)
                }

                // Назначаем нового командира
                employeeRepository.updateCommanderStatus(employee.id, true)
                squadRepository.updateSquadCommander(squad.id, employee.id)

                loadData()
                _toastMessage.postValue("${employee.name} назначен командиром")
            } catch (e: Exception) {
                _toastMessage.postValue("Ошибка назначения: ${e.localizedMessage}")
            }
        }
    }

    fun getAvailableSquads(currentSquadId: String?): LiveData<List<Squad>> {
        val result = MutableLiveData<List<Squad>>()
        viewModelScope.launch {
            val allSquads = squadRepository.getSquadByDepartment(department)
            result.postValue(allSquads.filter { it.id != currentSquadId })
        }
        return result
    }

    fun showSelectCommanderDialog(sectionId: String, members: List<Employee>) {
        val squad = sections.value?.find { it.id == sectionId }?.squad
        squad?.let {
            _selectCommanderEvent.value = SelectCommanderEvent(it, members)
        }
    }

    fun promoteEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                // Получаем текущий ранг
                val currentRank = Rank.entries.find { it.displayName == employee.rank }
                    ?: throw IllegalArgumentException("Invalid current rank")

                // Проверяем возможность повышения
                val nextRank = Rank.entries
                    .find { it.order == currentRank.order + 1 }
                    ?: run {
                        _toastMessage.postValue("Максимальное звание уже достигнуто")
                        return@launch
                    }

                // Обновляем сотрудника
                val updatedEmployee = employee.copy(rank = nextRank.displayName)
                employeeRepository.updateEmployee(updatedEmployee)

                // Обновляем список и показываем сообщение
                loadData()
                _toastMessage.postValue("${employee.name} повышен до ${nextRank.displayName}")

            } catch (e: Exception) {
                _toastMessage.postValue("Ошибка повышения: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    data class SelectCommanderEvent(val squad: Squad, val members: List<Employee>)
}