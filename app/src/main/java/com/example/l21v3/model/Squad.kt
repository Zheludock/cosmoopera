package com.example.l21v3.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "squad")
data class Squad(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String, // Предположим, что SquadType это String
    @ColumnInfo(index = true) var commanderId: String? = null,
    @ColumnInfo(index = true) var parentSquadId: String? = null,
    var currentSize: Int = 0,
    val maxSize: Int = calculateMaxSize(type)
) {
    companion object {
        fun calculateMaxSize(type: String): Int {
            return when (type) {
                "ЗВЕНО" -> 5
                "ВЗВОД" -> 4 * calculateMaxSize("ЗВЕНО") // 4 звена по 5 человек
                "РОТА" -> 2 * calculateMaxSize("ВЗВОД") // 2 взвода
                else -> 0
            }
        }
    }
}
