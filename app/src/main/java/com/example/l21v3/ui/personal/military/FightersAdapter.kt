package com.example.l21v3.ui.personal.military

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.R
import com.example.l21v3.model.Employee
import com.example.l21v3.databinding.ItemFighterBinding

class FightersAdapter(
    var expandedItems: Set<String>, // Состояние раскрытых элементов
    private val onItemClick: (String) -> Unit, // Обработка нажатия
    private val onActionClick: (Int, Employee) -> Unit // Обработка действий
) : ListAdapter<Employee, FightersAdapter.FighterViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FighterViewHolder {
        val binding = ItemFighterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FighterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FighterViewHolder, position: Int) {
        val employee = getItem(position)
        holder.bind(employee, expandedItems.contains(employee.id), onItemClick, onActionClick)
    }

    class FighterViewHolder(
        private val binding: ItemFighterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            employee: Employee,
            isExpanded: Boolean,
            onItemClick: (String) -> Unit,
            onActionClick: (Int, Employee) -> Unit
        ) {
            binding.name.text = employee.name
            binding.rank.text = employee.rank
            binding.unitName.text = employee.currentSquadId ?: "Нет звена"
            binding.commanderIcon.visibility = if (employee.isCommander) View.VISIBLE else View.GONE

            // Переключение видимости характеристик
            binding.attributesLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Обработка нажатия на имя
            binding.name.setOnClickListener {
                onItemClick(employee.id) // Передаем только ID сотрудника
            }

            // Обработка нажатия на actionIcon
            binding.actionIcon.setOnClickListener { view ->
                showPopupMenu(view, employee, onActionClick)
            }

            if (isExpanded) {
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
        }

        private fun showPopupMenu(
            anchorView: View,
            employee: Employee,
            onActionSelected: (Int, Employee) -> Unit
        ) {
            val popupMenu = PopupMenu(anchorView.context, anchorView)
            popupMenu.inflate(R.menu.fighter_actions_menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.giveLeftHandAmmo -> {
                        onActionSelected(LEFT_HAND, employee)
                        true
                    }
                    R.id.delete -> {
                        onActionSelected(DELETE_ACTION, employee)
                        true
                    }
                    R.id.giveRightHandAmmo -> {
                        onActionSelected(RIGHT_HAND, employee)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Employee>() {
            override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
                return oldItem == newItem
            }
        }

        const val DELETE_ACTION = 1
        const val RIGHT_HAND = 2
        const val LEFT_HAND = 3
    }
}