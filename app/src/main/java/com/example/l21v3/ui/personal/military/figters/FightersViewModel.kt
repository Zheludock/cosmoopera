package com.example.l21v3.ui.personal.military.figters

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
class FightersViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    // LiveData для списка бойцов
    val fighters: LiveData<List<Employee>> = repository.getEmployeesByRole("Military")

    // LiveData для хранения ID раскрытых элементов
    private val _expandedItems = MutableLiveData<Set<String>>(emptySet())
    val expandedItems: LiveData<Set<String>> get() = _expandedItems

    fun toggleAttributes(employeeId: String) {
        val currentSet = _expandedItems.value ?: emptySet()
        val newSet = if (currentSet.contains(employeeId)) {
            currentSet - employeeId
        } else {
            currentSet + employeeId
        }
        _expandedItems.value = newSet
    }

    fun giveRightHandAmmo(employee: Employee, newArm: String) {
        viewModelScope.launch {
            repository.updateRightHandArm(employee, newArm)
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.deleteEmployee(employee)
        }
    }

    fun giveLeftHandAmmo(employee: Employee, newArm: String) {
        viewModelScope.launch {
            repository.updateLeftHandArm(employee, newArm)
        }
    }
}