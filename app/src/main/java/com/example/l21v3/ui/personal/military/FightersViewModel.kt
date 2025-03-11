package com.example.l21v3.ui.personal.military

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.l21v3.model.Employee
import com.example.l21v3.model.data.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class FightersViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    // LiveData для списка бойцов
    private val _fighters = MutableLiveData<List<Employee>>()
    val fighters: LiveData<List<Employee>> get() = _fighters

    // LiveData для хранения ID раскрытых элементов
    private val _expandedItems = MutableLiveData<Set<String>>(emptySet())
    val expandedItems: LiveData<Set<String>> get() = _expandedItems

    init {
        loadFighters()
    }

    // Загрузка списка бойцов
    private fun loadFighters() {
        viewModelScope.launch {
            val fighters = repository.getEmployeesByRole("Military").value ?: emptyList()
            _fighters.value = fighters
        }
    }

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