package com.example.l21v3.model

class MonsterArmy(armySize: Int) {
    val acidfleaCount = (0..29).random() * armySize
    val littleBugCount = (0..99).random() * armySize
    val mediumbugCount = (0..49).random() * armySize
    val bigBugCount = (0..24).random() * armySize
    val mastodontCount: Int = run {
        val a = (0..99).random()
        when {
            a < 40 -> 0
            a < 93 -> if (armySize < 5) 1 else 2
            else -> if (armySize < 5) 2 else 5
        }
    }
    val moleCount = (0..14).random() * armySize
}