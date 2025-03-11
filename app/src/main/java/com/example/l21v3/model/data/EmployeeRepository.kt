package com.example.l21v3.model.data

import androidx.lifecycle.LiveData
import com.example.l21v3.model.Employee
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class EmployeeRepository @Inject constructor(
    private val employeeDao: EmployeeDao
) {
    suspend fun insert(employee: Employee) {
        employeeDao.insert(employee)
    }

    suspend fun update(employee: Employee) {
        employeeDao.update(employee)
    }

    suspend fun getEmployeeById(id: String): Employee? {
        return employeeDao.getById(id)
    }

    suspend fun getAllEmployees(): List<Employee> {
        return employeeDao.getAll()
    }

    suspend fun deleteEmployeeById(id: String) {
        employeeDao.deleteById(id)
    }

    suspend fun insertAll(employees: List<Employee>) {
        coroutineScope {
            employees.chunked(50) { chunk ->
                async { employeeDao.insertAll(chunk) }
            }.awaitAll()
        }
    }

    fun getEmployeesByRole(role: String): LiveData<List<Employee>> {
        return employeeDao.getEmployeesByRole(role)
    }
}