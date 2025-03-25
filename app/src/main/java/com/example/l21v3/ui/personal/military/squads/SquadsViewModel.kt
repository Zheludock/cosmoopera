package com.example.l21v3.ui.personal.military.squads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
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
    val squads: LiveData<List<Squad>> = _squads

    private val _squadMembers = MutableLiveData<Map<String, List<Employee>>>()
    val squadMembers: LiveData<Map<String, List<Employee>>> = _squadMembers

    private val _expandedSquadIds = MutableLiveData<Set<String>>(emptySet())
    val expandedSquadIds: LiveData<Set<String>> = _expandedSquadIds

    private val _freeEmployees = MutableLiveData<List<Employee>>()
    val freeEmployees: LiveData<List<Employee>> = _freeEmployees

    private val squadObserver = Observer<List<Squad>> { squads ->
        _squads.value = squads
    }

    private val freeEmployeesObserver = Observer<List<Employee>> { employees ->
        _freeEmployees.value = employees
    }

    private val memberObservers = mutableMapOf<String, Observer<List<Employee>>>()

    init {
        loadSquads()
        loadFreeEmployees()
    }

    private fun loadSquads() {
        squadRepository.getAllSquads().observeForever(squadObserver)
    }

    private fun loadFreeEmployees() {
        employeeRepository.getFreeEmployees("Military").observeForever(freeEmployeesObserver)
    }

    fun createNewSquad(squadName: String) {
        viewModelScope.launch {
            val newSquad = Squad(
                name = squadName,
                currentSize = 0
            )
            squadRepository.insertSquad(newSquad)
        }
    }

    fun loadSquadMembers(squadId: String) {
        // Удаляем предыдущий observer для этого отряда, если он был
        memberObservers[squadId]?.let { observer ->
            employeeRepository.getEmployeesBySquadId(squadId, "Military").removeObserver(observer)
        }

        // Создаем новый observer
        val observer = Observer<List<Employee>> { members ->
            _squadMembers.value?.let { currentMap ->
                val newMap = currentMap.toMutableMap()
                newMap[squadId] = members
                _squadMembers.value = newMap
            } ?: run {
                _squadMembers.value = mapOf(squadId to members)
            }
        }

        // Сохраняем observer и регистрируем его
        memberObservers[squadId] = observer
        employeeRepository.getEmployeesBySquadId(squadId, "Military").observeForever(observer)
    }

    fun toggleSquadExpansion(squadId: String) {
        val currentSet = _expandedSquadIds.value ?: emptySet()
        _expandedSquadIds.value = if (currentSet.contains(squadId)) {
            currentSet - squadId
        } else {
            currentSet + squadId
        }
    }

    fun addMembersToSquad(squadId: String, employeeIds: List<String>) {
        viewModelScope.launch {
            val squad = squadRepository.getSquadById(squadId)
            squad?.let {
                val newSize = it.currentSize + employeeIds.size
                if (newSize > Squad.MAXSIZE) return@launch

                employeeIds.forEach { employeeId ->
                    employeeRepository.updateEmployeeSquad(employeeId, squadId)
                }
                squadRepository.updateSquad(it.copy(currentSize = newSize))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Очищаем всех observers
        squadRepository.getAllSquads().removeObserver(squadObserver)
        employeeRepository.getFreeEmployees("Military").removeObserver(freeEmployeesObserver)
        memberObservers.forEach { (squadId, observer) ->
            employeeRepository.getEmployeesBySquadId(squadId, "Military").removeObserver(observer)
        }
    }
}

