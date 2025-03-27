package com.example.l21v3.ui.personal.engineers

import com.example.l21v3.model.data.EmployeeRepository
import com.example.l21v3.model.data.SquadRepository
import com.example.l21v3.ui.personal.BaseDepartmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EngineersViewModel @Inject constructor(
    employeeRepository: EmployeeRepository,
    squadRepository: SquadRepository
) : BaseDepartmentViewModel(employeeRepository, squadRepository) {
    override val role = "Engineer"
    override val department = "Engineers"

    init {
        onInit()
    }
}