package com.example.l21v3.model.data

import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val squadDao: SquadDao
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

    suspend fun getEmployeesByRole(role: String): List<Employee> {
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

    suspend fun updateEmployeeSquad(employee: Employee, squadId: String?) {
        updateEmployeeSquadDirect(employee.id, squadId)
    }

    private suspend fun updateEmployeeSquadDirect(employeeId: String, squadId: String?) = employeeDao.updateEmployeeSquadDirect(employeeId, squadId)

    suspend fun getEmployeesBySquadId(squadId: String, role: String): List<Employee> {
        return employeeDao.getEmployeesBySquadIdLiveData(squadId, role)
    }

    suspend fun getBySquad(squadId: String){
        employeeDao.getBySquad(squadId)
    }

    suspend fun updateCommanderStatus(oldCommanderId: String, b: Boolean) {
        employeeDao.updateCommanderStatus(oldCommanderId, b)
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

    suspend fun deleteSquadById(squadId: String){
        squadDao.deleteById(squadId)
    }

    suspend fun getAllSquads(): List<Squad> = squadDao.getAllSquads()

    suspend fun isSquadNameExists(name: String): Boolean = squadDao.isSquadNameExists(name)

    suspend fun updateSquadName(squadId:String, name: String){
        squadDao.updateName(squadId, name)
    }

    suspend fun updateSquadSize(squadId: String, delta: Int){
        squadDao.updateSize(squadId, delta)
    }

    suspend fun getSquadById(newSquadId: String): Squad? {
        return squadDao.getSquadById(newSquadId)
    }

    suspend fun updateSquadCommander(id: String, id1: String) {
        squadDao.updateCommander(id, id1)
    }
}
