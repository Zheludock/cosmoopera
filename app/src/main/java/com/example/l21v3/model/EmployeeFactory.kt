package com.example.l21v3.model

import kotlin.random.Random

object EmployeeFactory {
    private val names = listOf(
        "Алексей", "Мария", "Иван", "Ольга", "Сергей",
        "Анна", "Дмитрий", "Елена", "Андрей", "Наталья"
    )

    fun createEmployees(count: Int): List<Employee> {
        return List(count) { createEmployee() }
    }

    private fun createEmployee(): Employee {
        val name = names.random()
        val role = when ((0..99).random()) {
            in 0..9 -> "Science"
            in 10..19 -> "Engineer"
            in 20..24 -> "Doctor"
            else -> "Military"
        }
        val currentAbility = (50..100).random()
        val potencialAbility = (currentAbility..200).random()

        val characteristics = generateCharacteristics(currentAbility)

        return Employee(
            name = name,
            role = role,
            potencialAbility = potencialAbility,
            currentAbility = currentAbility,
            intellect = characteristics[0],
            communication = characteristics[1],
            professionalism = characteristics[2],
            ofp = characteristics[3],
            leadership = characteristics[4],
            ambitions = characteristics[5],
            temperament = characteristics[6],
            decisionMaking = characteristics[7],
            analysis = characteristics[8],
            creativity = characteristics[9],
            strategy = characteristics[10],
            economic = characteristics[11],
            disciplines = characteristics[12],
            uniqueSkill1 = characteristics[13],
            uniqueSkill2 = characteristics[14],
            uniqueSkill3 = characteristics[15],
            uniqueSkill4 = characteristics[16],
            uniqueSkill5 = characteristics[17],
        )
    }

    private fun generateCharacteristics(currentAbility: Int): List<Int> {
        val characteristics = MutableList(18) { 1 } // Все начинаются с 1
        var remaining = currentAbility - 18 // Остаток для распределения

        while (remaining > 0) {
            val index = Random.nextInt(18)
            if (characteristics[index] < 20) {
                characteristics[index] += 1
                remaining--
            }
        }

        return characteristics
    }

}