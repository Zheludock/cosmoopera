package com.example.l21v3

import android.app.Application
import android.util.Log
import com.example.l21v3.model.EmployeeFactory
import com.example.l21v3.model.Squad
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
            try {
                val count = database.employeeDao().getCount()
                Log.d("L21Application", "Количество сотрудников в базе данных: $count")

                if (count == 0) {
                    val employees = EmployeeFactory.createEmployees(100)
                    database.employeeDao().insertAll(employees)
                    val squads = listOf(
                        Squad(name = "Добыча ресурсов", department = "Engineers"),
                        Squad(name = "Робототехника", department = "Engineers"),
                        Squad(name = "Жизнеобеспечение", department = "Engineers"),
                        Squad(name = "Строительство", department = "Engineers"),
                        Squad(name = "Производство", department = "Engineers"),
                        Squad(name = "Реанимация", department = "Doctors"),
                        Squad(name = "Реабилитация", department = "Doctors"),
                        Squad(name = "Имплантология", department = "Doctors"),
                        Squad(name = "Кибенетизация", department = "Doctors"),
                        Squad(name = "Терапия", department = "Doctors"),
                        Squad(name = "Ксеноморфология", department = "Scientists"),
                        Squad(name = "Биология", department = "Scientists"),
                        Squad(name = "Физика", department = "Scientists"),
                        Squad(name = "Геология", department = "Scientists"),
                        Squad(name = "Метеорология", department = "Scientists"),
                    )
                    database.squadDao().insertAll(squads)
                    Log.d("L21Application", "Вставлено ${employees.size} сотрудников в базу данных.")
                } else {
                    Log.d("L21Application", "Сотрудники уже существуют в базе данных.")
                }
            } catch (e: Exception) {
                Log.e("L21Application", "Ошибка при работе с базой данных: ${e.message}")
            }
        }
    }

    override fun onTerminate() {
        applicationScope.cancel()
        super.onTerminate()
    }
}