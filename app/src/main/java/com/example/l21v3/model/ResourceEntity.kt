package com.example.l21v3.model

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.l21v3.R

@Entity(tableName = "resources")
data class ResourceEntity(
    @PrimaryKey
    @ColumnInfo(name = "resource_type")
    val type: ResourceType,
    val amount: Int
)

enum class ResourceType(
    @DrawableRes val iconRes: Int,
    val initialAmount: Int
) {
    IRON(R.drawable.ic_iron, 100),
    WATER(R.drawable.ic_water, 200),
    FOOD(R.drawable.ic_food, 150),
    CREDITS(R.drawable.ic_credits, 1000),
    ENERGY(R.drawable.ic_energy, 50),
    OIL(R.drawable.ic_oil, 20)
}