package com.example.l21v3.ui.personal.military

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import com.example.l21v3.model.data.EmployeeRepository
import com.example.l21v3.model.data.SquadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MilitaryViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val squadRepository: SquadRepository
) : ViewModel() {

    private val _sections = MutableLiveData<List<Section>>()
    val sections: LiveData<List<Section>> = _sections

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val employees = employeeRepository.getEmployeesByRole("Military")
                val squads = squadRepository.getAllSquads().sortedBy { it.name }

                val sections = mutableListOf<Section>().apply {
                    // Секция "Без звена" (без максимального размера)
                    add(Section(
                        id = "no_squad",
                        title = "Без отряда",
                        employees = employees.filter { it.currentSquadId == null }
                    ))

                    // Секции отрядов
                    squads.forEach { squad ->
                        add(Section(
                            id = squad.id,
                            title = squad.name,
                            employees = employees.filter { it.currentSquadId == squad.id },
                            maxSize = squad.maxSize
                        ))
                    }
                }
                _sections.value = sections
            } catch (e: Exception) {
                _toastMessage.value = "Ошибка загрузки данных"
            }
        }
    }

    fun createSquad(name: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _toastMessage.value = "Введите название отряда"
                return@launch
            }

            if (squadRepository.isSquadNameExists(name)) {
                _toastMessage.value = "Отряд с таким именем уже существует"
                return@launch
            }

            val newSquad = Squad(
                id = UUID.randomUUID().toString(),
                name = name,
                currentSize = 0
            )

            try {
                squadRepository.insertSquad(newSquad)
                loadData() // Обновляем данные после успешного создания
            } catch (e: Exception) {
                _toastMessage.value = "Ошибка создания отряда"
            }
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            employeeRepository.deleteEmployee(employee)
        }
    }

    fun giveRightHandAmmo(employee: Employee, newArm: String){
        viewModelScope.launch {
            employeeRepository.updateRightHandArm(employee, newArm)
        }
    }

    fun giveLeftHandAmmo(employee: Employee, newArm: String){
        viewModelScope.launch {
            employeeRepository.updateLeftHandArm(employee, newArm)
        }
    }

    fun getAvailableSquads(excludeSquadId: String?): LiveData<List<Squad>> {
        val result = MutableLiveData<List<Squad>>()
        viewModelScope.launch {
            val allSquads = squadRepository.getAllSquads()
            val filtered = allSquads.filter { it.id != excludeSquadId }
            result.postValue(filtered)
        }
        return result
    }

    fun transferEmployee(employee: Employee, newSquadId: String?) {
        viewModelScope.launch {
            try {
                // Проверяем целевой отряд (если это не "без отряда")
                if (newSquadId != null) {
                    val targetSquad = squadRepository.getSquadById(newSquadId)
                    if (targetSquad != null) {
                        if (targetSquad.currentSize >= Squad.MAXSIZE) {
                            _toastMessage.postValue("Отряд заполнен (макс. ${Squad.MAXSIZE} человек)")
                            return@launch
                        }
                    }
                }

                // Обновляем данные сотрудника
                val updatedEmployee = employee.copy(currentSquadId = newSquadId)
                employeeRepository.updateEmployee(updatedEmployee)

                // Обновляем размеры отрядов
                newSquadId?.let {
                    squadRepository.updateSquadSize(it, 1)
                }

                employee.currentSquadId?.let { oldSquadId ->
                    squadRepository.updateSquadSize(oldSquadId, -1)
                }

                loadData()
                _toastMessage.postValue(
                    if (newSquadId != null) "Сотрудник переведен"
                    else "Сотрудник исключен из отряда"
                )
            } catch (e: Exception) {
                _toastMessage.postValue("Ошибка: ${e.message}")
            }
        }
    }

    fun deleteSquad(squadId: String) {
        viewModelScope.launch {
            try {
                // Перемещаем сотрудников в "Без звена"
                employeeRepository.getEmployeesBySquadId(squadId, "Military").forEach { employee ->
                    employeeRepository.updateEmployeeSquad(employee, null)
                }

                squadRepository.deleteSquadById(squadId)
                loadData()
            } catch (e: Exception) {
                _toastMessage.postValue("Ошибка удаления отряда")
            }
        }
    }

    fun renameSquad(squadId: String, newName: String) {
        viewModelScope.launch {
            try {
                if (newName.isBlank()) {
                    _toastMessage.postValue("Введите новое название")
                    return@launch
                }

                if (squadRepository.isSquadNameExists(newName)) {
                    _toastMessage.postValue("Имя уже используется")
                    return@launch
                }

                squadRepository.updateSquadName(squadId, newName)
                loadData()
            } catch (e: Exception) {
                _toastMessage.postValue("Ошибка переименования")
            }
        }
    }
    private val _selectCommanderEvent = MutableLiveData<SelectCommanderEvent>()
    val selectCommanderEvent: LiveData<SelectCommanderEvent> = _selectCommanderEvent

    fun showSelectCommanderDialog(squadId: String) {
        viewModelScope.launch {
            try {
                val squad = squadRepository.getSquadById(squadId)
                val members = employeeRepository.getEmployeesBySquadId(squadId, "Military")
                _selectCommanderEvent.postValue(squad?.let { SelectCommanderEvent(it, members) })
            } catch (e: Exception) {
                _toastMessage.postValue("Ошибка загрузки данных")
            }
        }
    }

    fun assignCommander(squad: Squad, employee: Employee) {
        viewModelScope.launch {
            try {
                // Снимаем статус командира с предыдущего
                squad.commanderId?.let { oldCommanderId ->
                    employeeRepository.updateCommanderStatus(oldCommanderId, false)
                }

                // Назначаем нового командира
                employeeRepository.updateCommanderStatus(employee.id, true)
                squadRepository.updateSquadCommander(squad.id, employee.id)

                loadData()
                _toastMessage.postValue("${employee.name} назначен командиром")
            } catch (e: Exception) {
                _toastMessage.postValue("Ошибка назначения командира")
            }
        }
    }

    data class SelectCommanderEvent(val squad: Squad, val members: List<Employee>)
}