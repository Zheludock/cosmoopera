package com.example.l21v3.ui.personal

import androidx.lifecycle.ViewModel
import com.example.l21v3.model.data.EmployeeRepository
import com.example.l21v3.model.data.SquadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonalViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val squadRepository: SquadRepository
) : ViewModel() {

}