package com.example.l21v3.model.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideEmployeeDao(appDatabase: AppDatabase): EmployeeDao {
        return appDatabase.employeeDao()
    }

    @Provides
    @Singleton
    fun provideSquadDao(appDatabase: AppDatabase): SquadDao {
        return appDatabase.squadDao()
    }
}
