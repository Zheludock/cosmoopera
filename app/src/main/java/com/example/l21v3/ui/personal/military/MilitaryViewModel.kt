package com.example.l21v3.ui.personal.military

import androidx.lifecycle.viewModelScope
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import com.example.l21v3.model.data.EmployeeRepository
import com.example.l21v3.model.data.SquadRepository
import com.example.l21v3.ui.personal.BaseDepartmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MilitaryViewModel @Inject constructor(
    employeeRepository: EmployeeRepository,
    squadRepository: SquadRepository
) : BaseDepartmentViewModel(employeeRepository, squadRepository) {
    override val role = "Military"
    override val department = "Military"

    init {
        onInit()
    }

    fun createSquad(name: String, department: String) {
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
                currentSize = 0,
                department = department,
            )

            try {
                squadRepository.insertSquad(newSquad)
                loadData() // Обновляем данные после успешного создания
            } catch (e: Exception) {
                _toastMessage.value = "Ошибка создания отряда"
            }
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

    override fun transferEmployee(employee: Employee, newSquadId: String?) {
        viewModelScope.launch {
            try {
                if (newSquadId != null) {
                    val targetSquad = squadRepository.getSquadById(newSquadId)
                    if (targetSquad != null && targetSquad.currentSize >= Squad.MAXSIZE) {
                        _toastMessage.postValue("Отряд заполнен (макс. ${Squad.MAXSIZE} человек)")
                        return@launch
                    }
                }

                super.transferEmployee(employee, newSquadId)
                newSquadId?.let { squadRepository.updateSquadSize(it, 1) }
                employee.currentSquadId?.let { squadRepository.updateSquadSize(it, -1) }
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
}