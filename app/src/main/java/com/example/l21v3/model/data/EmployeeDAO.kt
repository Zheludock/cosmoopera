package com.example.l21v3.model.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.l21v3.model.Employee

@Dao
interface EmployeeDao {
    @Insert
    suspend fun insert(employee: Employee)

    @Update
    suspend fun update(employee: Employee)

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getById(id: String): Employee?

    @Query("SELECT * FROM employees")
    suspend fun getAll(): List<Employee>

    @Query("SELECT * FROM employees WHERE role = :role")
    fun getEmployeesByRole(role: String): LiveData<List<Employee>>

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteById(id: String)

    @Insert
    suspend fun insertAll(employees: List<Employee>)

    @Query("SELECT COUNT(*) FROM employees")
    suspend fun getCount(): Int
}