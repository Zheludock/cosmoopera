package com.example.l21v3.model.data


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.l21v3.model.Employee

@Database(entities = [Employee::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
}