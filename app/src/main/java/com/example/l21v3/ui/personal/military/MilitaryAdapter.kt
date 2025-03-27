package com.example.l21v3.ui.personal.military

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.R
import com.example.l21v3.databinding.ItemEmployeeBinding
import com.example.l21v3.databinding.ItemSectionHeaderBinding
import com.example.l21v3.model.Employee

class MilitaryAdapter(
    private val onHeaderClick: (String) -> Unit,
    private val onItemClick: (String) -> Unit,
    private val onHeaderAction: (Section, View) -> Unit,
    private val onEmployeeAction: (Employee, View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val expandedSections = mutableSetOf<String>()
    private val expandedItems = mutableSetOf<String>()
    private var sections: List<Section> = emptyList()
    private val items = mutableListOf<ListItem>()

    fun toggleSection(sectionId: String) {
        if (expandedSections.contains(sectionId)) {
            expandedSections.remove(sectionId)
        } else {
            expandedSections.add(sectionId)
        }
        updateItems()
    }

    fun toggleItem(itemId: String) {
        if (expandedItems.contains(itemId)) {
            expandedItems.remove(itemId)
        } else {
            expandedItems.add(itemId)
        }
        updateItems()
    }

    fun submitSections(newSections: List<Section>) {
        sections = newSections
        updateItems()
    }

    private fun updateItems() {
        items.clear()
        sections.forEach { section ->
            items.add(ListItem.Header(section))
            if (expandedSections.contains(section.id)) {
                section.employees.forEach { employee ->
                    items.add(ListItem.EmployeeItem(employee))
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemSectionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_ITEM -> EmployeeViewHolder(
                ItemEmployeeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val header = items[position] as ListItem.Header
                val isExpanded = expandedSections.contains(header.section.id)
                holder.bind(header.section, isExpanded)

                holder.itemView.setOnClickListener {
                    onHeaderClick(header.section.id)
                }

                holder.binding.ivActionIcon.setOnClickListener { view ->
                    if (header.section.id != "no_squad") {
                        onHeaderAction(header.section, view)
                    }
                }
            }
            is EmployeeViewHolder -> {
                val employeeItem = items[position] as ListItem.EmployeeItem
                val isExpanded = expandedItems.contains(employeeItem.employee.id)
                holder.bind(employeeItem.employee, isExpanded)

                holder.itemView.setOnClickListener {
                    onItemClick(employeeItem.employee.id)
                }

                holder.binding.actionIcon.setOnClickListener { view ->
                    onEmployeeAction(employeeItem.employee, view)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.Header -> VIEW_TYPE_HEADER
            is ListItem.EmployeeItem -> VIEW_TYPE_ITEM
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}


class HeaderViewHolder(
    val binding: ItemSectionHeaderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(section: Section, isExpanded: Boolean) {
        binding.tvTitle.text = section.title
        binding.tvSquadSize.text = section.sizeText

        // Устанавливаем цвет в зависимости от заполненности отряда
        val color = if (section.isUnderstaffed && section.id != "no_squad") {
            ContextCompat.getColor(binding.root.context, R.color.red)
        } else {
            ContextCompat.getColor(binding.root.context, android.R.color.secondary_text_dark)
        }
        binding.tvSquadSize.setTextColor(color)
    }
}

class EmployeeViewHolder(
    val binding: ItemEmployeeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(employee: Employee, isExpanded: Boolean) {
        binding.name.text = employee.name
        binding.rank.text = employee.rank
        binding.commanderIcon.visibility = if (employee.isCommander) View.VISIBLE else View.GONE
        binding.attributesLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
        binding.leftHandIcon.setImageResource(getWeaponIconRes(employee.leftHandArm))
        binding.rightHandIcon.setImageResource(getWeaponIconRes(employee.rightHandArm))

        // Заполнение атрибутов
        binding.intellect.text = "Интеллект: ${employee.intellect}"
        binding.communication.text = "Коммуникабельность: ${employee.communication}"
        binding.professionalism.text = "Интеллект: ${employee.professionalism}"
        binding.fitness.text = "Физическая подготовка: ${employee.ofp}"
        binding.leadership.text = "Лидерство: ${employee.leadership}"
        binding.ambition.text = "Амбиции: ${employee.ambitions}"
        binding.temperament.text = "Темперамент: ${employee.temperament}"
        binding.desisionMaking.text = "Принятие решений: ${employee.decisionMaking}"
        binding.analysis.text = "Анализ: ${employee.analysis}"
        binding.creativity.text = "Креативность: ${employee.creativity}"
        binding.strategy.text = "Стратегия: ${employee.strategy}"
        binding.economic.text = "Экономика: ${employee.economic}"
        binding.discipline.text = "Дисциплина: ${employee.disciplines}"
        binding.unique1.text = "Меткость: ${employee.uniqueSkill1}"
        binding.unique2.text = "Выбор позиции: ${employee.uniqueSkill2}"
        binding.unique3.text = "Реакция: ${employee.uniqueSkill3}"
        binding.unique4.text = "Тактика: ${employee.uniqueSkill4}"
        binding.unique5.text = "Самообладание: ${employee.uniqueSkill5}"
    }

    private fun getWeaponIconRes(weaponType: String?): Int {
        return when (weaponType?.lowercase()) {
            "pistolet" -> R.drawable.ic_pistolet
            "sword" -> R.drawable.ic_sword_02
            "grenade" -> R.drawable.ic_bomb
            "sniper" -> R.drawable.ic_sniper
            "automat" -> R.drawable.ic_automat
            "shotgun" -> R.drawable.ic_shotgun
            "heavygun" -> R.drawable.ic_heavygun
            else -> R.drawable.ic_fitness // Иконка для отсутствующего оружия
        }
    }
}

sealed class ListItem {
    data class Header(val section: Section) : ListItem()
    data class EmployeeItem(val employee: Employee) : ListItem() // Переименовываем класс
}