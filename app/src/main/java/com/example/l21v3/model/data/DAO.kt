package com.example.l21v3.model.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.l21v3.model.Employee
import com.example.l21v3.model.ResourceEntity
import com.example.l21v3.model.Squad
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Insert
    suspend fun insert(employee: Employee)

    @Update
    suspend fun update(employee: Employee)

    @Query("SELECT * FROM employee WHERE currentSquadId IS :squadId AND role = :role")
    suspend fun getEmployeesBySquadIdLiveData(squadId: String?, role: String): List<Employee>

    @Delete
    suspend fun delete(employee: Employee)

    @Query("SELECT COUNT(*) FROM employee WHERE currentSquadId = :squadId")
    suspend fun getSquadMembersCount(squadId: String): Int

    @Query("SELECT * FROM employee WHERE id = :id")
    suspend fun getEmployeeById(id: String): Employee?

    @Transaction
    @Query("SELECT * FROM employee WHERE id = :employeeId")
    suspend fun getEmployeeWithSquad(employeeId: String): EmployeeWithSquad

    @Query("SELECT * FROM employee WHERE role = :role")
    suspend fun getEmployeesByRole(role: String): List<Employee>

    @Query("SELECT COUNT(*) FROM employee")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(employees: List<Employee>)

    @Query("UPDATE employee SET currentSquadId = :squadId WHERE id = :id")
    suspend fun updateEmployeeSquadDirect(id: String, squadId: String?)

    @Query("SELECT * FROM employee WHERE currentSquadId = :squadId")
    suspend fun getBySquad(squadId: String): List<Employee>

    @Query("UPDATE employee SET isCommander = :isCommander WHERE id = :employeeId")
    suspend fun updateCommanderStatus(employeeId: String, isCommander: Boolean)

    @Query("SELECT * FROM employee WHERE currentSquadId = :squadId")
    suspend fun getEmployeesBySquadId(squadId: String): List<Employee>
}

@Dao
interface SquadDao {
    @Insert
    suspend fun insert(squad: Squad)

    @Update
    suspend fun update(squad: Squad)

    @Query("UPDATE squad SET name = :newName WHERE id = :squadId")
    suspend fun updateName(squadId: String, newName: String)

    @Query("UPDATE squad SET currentSize = currentSize + :delta WHERE id = :squadId")
    suspend fun updateSize(squadId: String, delta: Int)

    @Delete
    suspend fun delete(squad: Squad)

    @Query("DELETE FROM squad WHERE id = :squadId")
    suspend fun deleteById(squadId: String)

    @Query("SELECT * FROM squad WHERE id = :id")
    suspend fun getSquadById(id: String): Squad?

    @Query("SELECT * FROM squad WHERE commanderId = :commanderId")
    suspend fun getSquadsByCommanderId(commanderId: String): List<Squad>

    @Transaction
    @Query("SELECT * FROM squad WHERE id = :squadId")
    suspend fun getSquadWithMembers(squadId: String): SquadWithMembers

    @Query("SELECT * FROM squad")
    suspend fun getAllSquads(): List<Squad>

    @Query("SELECT EXISTS(SELECT 1 FROM squad WHERE name = :name)")
    suspend fun isSquadNameExists(name: String): Boolean

    @Query("UPDATE squad SET commanderId = :commanderId WHERE id = :squadId")
    suspend fun updateCommander(squadId: String, commanderId: String?)

    @Query("SELECT * FROM squad WHERE department = :department")
    suspend fun getSquadByDepartment(department: String): List<Squad>

    @Insert
    suspend fun insertAll(squads: List<Squad>)
}

@Dao
interface ResourcesDao {
    @Query("SELECT * FROM resources")
    suspend fun getAll(): List<ResourceEntity>

    @Query("SELECT * FROM resources")
    fun getAllStream(): Flow<List<ResourceEntity>>

    @Upsert
    suspend fun upsert(resource: ResourceEntity)
}