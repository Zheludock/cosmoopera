package com.example.l21v3.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.l21v3.model.data.Rank
import java.util.UUID

@Entity(
    tableName = "employee",
    foreignKeys = [ForeignKey(
        entity = Squad::class,
        parentColumns = ["id"],
        childColumns = ["currentSquadId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Employee(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String,
    var rank: String = Rank.PRIVATE.displayName,
    var isOnMission: Boolean = false,
    var isCommander: Boolean = false,
    @ColumnInfo(index = true) var currentSquadId: String? = null,
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


