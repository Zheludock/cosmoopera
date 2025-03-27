package com.example.l21v3.ui.personal.military

import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad

data class Section(
    val id: String,
    val title: String,
    val employees: List<Employee>,
    val maxSize: Int? = null,
    val squad: Squad? = null
) {
    val currentSize: Int get() = employees.size
    val sizeText: String get() = if (maxSize != null) "$currentSize/$maxSize" else "$currentSize"
    val isUnderstaffed: Boolean get() = maxSize?.let { currentSize < it } ?: false
}