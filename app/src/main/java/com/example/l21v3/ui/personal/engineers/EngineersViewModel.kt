package com.example.l21v3.ui.personal.engineers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import com.example.l21v3.model.data.EmployeeRepository
import com.example.l21v3.model.data.SquadRepository
import com.example.l21v3.ui.personal.military.Section
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EngineersViewModel @Inject constructor(
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
                val employees = employeeRepository.getEmployeesByRole("Engineer")
                val squads = squadRepository.getSquadByDepartment("Engineers")

                val sections = mutableListOf<Section>().apply {
                    // Добавляем секцию "Без отряда"
                    add(Section(
                        id = "no_squad",
                        title = "Без отряда",
                        employees = employees.filter { it.currentSquadId == null }
                    ))

                    // Добавляем остальные отряды
                    squads.forEach { squad ->
                        add(Section(
                            id = squad.id,
                            title = squad.name,
                            employees = employees.filter { it.currentSquadId == squad.id },
                            squad = squad,
                            maxSize = null // Явно отключаем максимальный размер
                        ))
                    }
                }

                _sections.value = sections
            } catch (e: Exception) {
                _toastMessage.value = "Ошибка загрузки данных"
            }
        }
    }

    fun transferEmployee(employee: Employee, newSquadId: String?) {
        viewModelScope.launch {
            try {
                // Для инженеров не проверяем максимальный размер
                val updatedEmployee = employee.copy(currentSquadId = newSquadId)
                employeeRepository.updateEmployee(updatedEmployee)
                loadData()
            } catch (e: Exception) {
                _toastMessage.postValue("Ошибка перевода сотрудника")
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

    fun getAvailableSquads(currentSquadId: String?): LiveData<List<Squad>> {
        val result = MutableLiveData<List<Squad>>()
        viewModelScope.launch {
            val allSquads = squadRepository.getSquadByDepartment("Engineers")
            result.postValue(allSquads.filter { it.id != currentSquadId })
        }
        return result
    }

    private val _selectCommanderEvent = MutableLiveData<SelectCommanderEvent>()
    val selectCommanderEvent: LiveData<SelectCommanderEvent> = _selectCommanderEvent

    fun showSelectCommanderDialog(sectionId: String, members: List<Employee>) {
        val squad = sections.value?.find { it.id == sectionId }?.squad
        squad?.let {
            _selectCommanderEvent.value = SelectCommanderEvent(it, members)
        }
    }

    data class SelectCommanderEvent(val squad: Squad, val members: List<Employee>)
}