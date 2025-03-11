package com.example.l21v3.ui.personal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.l21v3.model.Employee
import com.example.l21v3.model.data.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {
    val militaryEmployees: LiveData<List<Employee>> = repository.getEmployeesByRole("Military")

    // LiveData для сообщений об ошибках или статусах
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    // Обновление сотрудника
    fun updateEmployee(employee: Employee?) {
        if (employee == null) {
            _message.value = "Сотрудник не выбран"
            return
        }
        viewModelScope.launch {
            try {
                repository.update(employee)
                _message.value = "Сотрудник обновлен"
            } catch (e: Exception) {
                _message.value = "Ошибка при обновлении сотрудника: ${e.message}"
            }
        }
    }

    // Удаление сотрудника
    fun deleteEmployee(employee: Employee?) {
        if (employee == null) {
            _message.value = "Сотрудник не выбран"
            return
        }
        viewModelScope.launch {
            try {
                repository.deleteEmployeeById(employee.id)
                _message.value = "Сотрудник удален"
            } catch (e: Exception) {
                _message.value = "Ошибка при удалении сотрудника: ${e.message}"
            }
        }
    }

    // Добавление нового сотрудника
    fun addEmployee(employee: Employee?) {
        if (employee == null) {
            _message.value = "Сотрудник не выбран"
            return
        }
        viewModelScope.launch {
            try {
                repository.insert(employee)
                _message.value = "Сотрудник добавлен"
            } catch (e: Exception) {
                _message.value = "Ошибка при добавлении сотрудника: ${e.message}"
            }
        }
    }
}