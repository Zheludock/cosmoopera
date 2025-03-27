package com.example.l21v3.ui.personal.doctors

import com.example.l21v3.model.data.EmployeeRepository
import com.example.l21v3.model.data.SquadRepository
import com.example.l21v3.ui.personal.BaseDepartmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoctorsViewModel @Inject constructor(
    employeeRepository: EmployeeRepository,
    squadRepository: SquadRepository
) : BaseDepartmentViewModel(employeeRepository, squadRepository) {
    override val role = "Doctor"
    override val department = "Doctors"
    init {
        onInit()
    }
}