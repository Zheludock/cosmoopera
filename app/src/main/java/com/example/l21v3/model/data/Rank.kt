package com.example.l21v3.model.data

enum class Rank(
    val displayName: String,
    val order: Int,
    val salaryMultiplier: Float
) {
    PRIVATE("Рядовой", 1, 1.0f),
    CORPORAL("Ефрейтор", 2, 1.2f),
    JSERGEANT("Младший Сержант", 3, 1.5f),
    SERGEANT("Сержант", 4, 2.0f),
    SSERGEANT("Старший сержант", 5, 2.5f),
    SENIOR("Старшина", 6, 3.0f),
    PRAPOR("Прапорщик", 7, 3.3f),
    SPRAPOR("Старший прапорщик", 8, 3.5f),
    JLIEUTENANT("Младший лейтенант", 9, 5.0f),
    LIEUTENANT("Лейтенант", 10, 6.0f),
    SLIEUTENANT("Старший лейтенант", 11, 7.0f),
    CAPTAIN("Капитан", 12, 8.0f),
    MAJOR("Майор", 13, 10.0f),
    JCOLONEL("Подполковник", 14,12.0f),
    COLONEL("Полковник", 15, 15.0f);
}