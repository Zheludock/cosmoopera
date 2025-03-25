package com.example.l21v3.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "squad")
data class Squad(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    @ColumnInfo(index = true) var commanderId: String? = null,
    var currentSize: Int = 0,
    val maxSize: Int = MAXSIZE
) {
    companion object {
        const val MAXSIZE: Int = 5
    }
}
