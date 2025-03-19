package com.example.l21v3.ui.personal.military

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import com.example.l21v3.model.Subunit
import com.example.l21v3.model.data.EmployeeRepository
import com.example.l21v3.model.data.SquadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SquadsViewModel @Inject constructor(
    private val squadRepository: SquadRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _squads = MutableLiveData<List<Squad>>()
    val squads: LiveData<List<Squad>> get() = _squads

    private val _expandedSquads = MutableLiveData<Set<String>>(emptySet())
    val expandedSquads: LiveData<Set<String>> get() = _expandedSquads

    init {
        loadSquads()
    }

    fun toggleSquadExpansion(squadId: String) {
        val currentSet = _expandedSquads.value ?: emptySet()
        val newSet = if (currentSet.contains(squadId)) {
            currentSet - squadId
        } else {
            currentSet + squadId
        }
        _expandedSquads.value = newSet
    }

    suspend fun getSquadMembers(squadId: String): List<Employee> {
        return employeeRepository.getEmployeesBySquadId(squadId)
    }

    fun createSquad(name: String, type: String) {
        viewModelScope.launch {
            val squad = Squad(
                name = name,
                type = type // Тип отряда
            )
            squadRepository.insertSquad(squad)
            loadSquads() // Обновляем список отрядов после добавления
        }
    }

    private val _freeSubunits = MutableLiveData<List<Subunit>>()
    val freeSubunits: LiveData<List<Subunit>> get() = _freeSubunits

    // Загрузка свободных подотрядов
    fun loadFreeSubunits(squadType: String) {
        viewModelScope.launch {
            val subunits: List<Subunit> = when (squadType) {
                "ЗВЕНО" -> employeeRepository.getFreeEmployees().map { Subunit.EmployeeSubunit(it) }
                "ВЗВОД", "РОТА" -> squadRepository.getFreeSquads(null).map { Subunit.SquadSubunit(it) }
                else -> emptyList()
            }
            _freeSubunits.value = subunits
        }
    }

    // Добавление сотрудника в отряд
    fun addEmployeeToSquad(squadId: String, employeeId: String) {
        viewModelScope.launch {
            employeeRepository.updateEmployeeSquad(employeeId, squadId)
            loadSquads() // Обновляем список отрядов
        }
    }

    // Добавление подотряда в отряд
    fun addSubunitToSquad(squadId: String, subunitId: String) {
        viewModelScope.launch {
            squadRepository.updateSquadParent(subunitId, squadId)
            loadSquads() // Обновляем список отрядов
        }
    }

    // Загрузка списка отрядов
    private fun loadSquads() {
        viewModelScope.launch {
            _squads.value = squadRepository.getAllSquads()
        }
    }
}
