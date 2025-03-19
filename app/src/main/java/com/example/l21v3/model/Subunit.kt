package com.example.l21v3.model

sealed class Subunit {
    data class EmployeeSubunit(val employee: Employee) : Subunit()
    data class SquadSubunit(val squad: Squad) : Subunit()
}