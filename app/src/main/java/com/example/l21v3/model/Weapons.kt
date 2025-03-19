package com.example.l21v3.model

enum class Weapons(damage: Int) {
    PISTOLET(20), GRENADE(50), SNIPER(150), AUTOMAT(50), SHOTGUN(70), HEAVYGUN(100), SWORD(70);
    companion object {
        fun fromString(value: String?): Weapons? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}