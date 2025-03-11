package com.example.l21v3

import android.app.Application
import com.example.l21v3.model.EmployeeFactory
import com.example.l21v3.model.data.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@HiltAndroidApp
class L21 : Application() {
    @Inject
    lateinit var database: AppDatabase
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
//            if (database.employeeDao().getCount() == 0) {
//                val employees = EmployeeFactory.createEmployees(100)
//                database.employeeDao().insertAll(employees)
//            }
        }
    }

    override fun onTerminate() {
        applicationScope.cancel()
        super.onTerminate()
    }
}