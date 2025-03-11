package com.example.l21v3.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String,
    var rank: String = "Рядовой",
    var isOnMission: Boolean = false,
    var isCommander: Boolean = false,
    var currentUnitId: String? = null,
    val potencialAbility: Int,
    var currentAbility: Int,
    var intellect: Int,
    var communication: Int,
    var professionalism: Int,
    var ofp: Int,
    var leadership: Int,
    var ambitions: Int,
    var temperament: Int,
    var decisionMaking: Int,
    var analysis: Int,
    var creativity: Int,
    var strategy: Int,
    var economic: Int,
    var disciplines: Int,
    var uniqueSkill1: Int,
    var uniqueSkill2: Int,
    var uniqueSkill3: Int,
    var uniqueSkill4: Int,
    var uniqueSkill5: Int,
    var rightHandArm: String? = "Pistolet",
    var leftHandArm: String? = null,
)


