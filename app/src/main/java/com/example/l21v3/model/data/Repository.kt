package com.example.l21v3.model.data

import androidx.lifecycle.LiveData
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    private val employeeDao: EmployeeDao
) {
    suspend fun insertEmployee(employee: Employee) {
        employeeDao.insert(employee)
    }

    suspend fun updateEmployee(employee: Employee) {
        employeeDao.update(employee)
    }

    suspend fun deleteEmployee(employee: Employee) {
        employeeDao.delete(employee)
    }

    suspend fun getEmployeeById(id: String): Employee? {
        return employeeDao.getEmployeeById(id)
    }

    suspend fun getEmployeeWithSquad(employeeId: String): EmployeeWithSquad {
        return employeeDao.getEmployeeWithSquad(employeeId)
    }

    fun getEmployeesByRole(role: String): LiveData<List<Employee>> {
        return employeeDao.getEmployeesByRole(role)
    }

    suspend fun updateLeftHandArm(employee: Employee, newArm: String) {
        employee.leftHandArm = newArm
        return employeeDao.update(employee)
    }

    suspend fun updateRightHandArm(employee: Employee, newArm: String) {
        employee.rightHandArm = newArm
        return employeeDao.update(employee)
    }

    suspend fun getCount(): Int {
        return employeeDao.getCount()
    }

    suspend fun insertAll(employees: List<Employee>) {
        employeeDao.insertAll(employees)
    }

    fun getFreeEmployees(role: String): LiveData<List<Employee>> =
        employeeDao.getEmployeesBySquadIdLiveData(null, role)

    suspend fun updateEmployeeSquad(employeeId: String, squadId: String) {
        employeeDao.updateEmployeeSquad(employeeId, squadId)
    }

    fun getEmployeesBySquadId(squadId: String, role: String): LiveData<List<Employee>> {
        return employeeDao.getEmployeesBySquadIdLiveData(squadId, role)
    }
}

@Singleton
class SquadRepository @Inject constructor(
    private val squadDao: SquadDao,
    private val employeeDao: EmployeeDao
) {
    suspend fun insertSquad(squad: Squad) {
        squadDao.insert(squad)
    }

    suspend fun updateSquad(squad: Squad) {
        squadDao.update(squad)
    }

    suspend fun deleteSquad(squad: Squad) {
        squadDao.delete(squad)
    }

    suspend fun getSquadById(id: String): Squad? {
        return squadDao.getSquadById(id)
    }

    suspend fun getSquadsByCommanderId(commanderId: String): List<Squad> {
        return squadDao.getSquadsByCommanderId(commanderId)
    }

    suspend fun getSquadWithMembers(squadId: String): SquadWithMembers {
        return squadDao.getSquadWithMembers(squadId)
    }

    fun getAllSquads(): LiveData<List<Squad>> = squadDao.getAllSquads()
}
