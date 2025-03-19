package com.example.l21v3.model.data

import androidx.room.Embedded
import androidx.room.Relation
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad

data class EmployeeWithSquad(
    @Embedded val employee: Employee,
    @Relation(
        parentColumn = "currentSquadId",
        entityColumn = "id"
    )
    val squad: Squad?
)

data class SquadWithMembers(
    @Embedded val squad: Squad,
    @Relation(
        parentColumn = "id",
        entityColumn = "currentSquadId"
    )
    val members: List<Employee>
)