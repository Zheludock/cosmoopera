package com.example.l21v3.ui.personal.military

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.l21v3.model.Employee
import com.example.l21v3.model.data.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MilitaryViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    // LiveData для списка военных сотрудников
    val militaryEmployees: LiveData<List<Employee>> = repository.getEmployeesByRole("Military")

    // MutableState для хранения ID раскрытых элементов
    private val _expandedItems = MutableLiveData<Set<String>>(emptySet())
    val expandedItems: LiveData<Set<String>> get() = _expandedItems

    // MutableLiveData для сообщений об ошибках или статусах
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    // Переключение видимости характеристик
    fun toggleAttributes(employeeId: String?) {
        if (employeeId == null) {
            _message.value = "Сотрудник не выбран"
            return
        }
        val currentSet = _expandedItems.value ?: emptySet()
        _expandedItems.value = if (currentSet.contains(employeeId)) {
            currentSet - employeeId
        } else {
            currentSet + employeeId
        }
    }
}