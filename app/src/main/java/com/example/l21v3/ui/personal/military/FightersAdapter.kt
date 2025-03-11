package com.example.l21v3.ui.personal.military

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.model.Employee
import com.example.l21v3.databinding.ItemFighterBinding

class FightersAdapter(
    var expandedItems: Set<String>, // Состояние раскрытых элементов
    private val onItemClick: (String) -> Unit // Обработка нажатия
) : ListAdapter<Employee, FightersAdapter.FighterViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FighterViewHolder {
        val binding = ItemFighterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FighterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FighterViewHolder, position: Int) {
        val employee = getItem(position)
        holder.bind(employee, expandedItems.contains(employee.id), onItemClick)
    }

    class FighterViewHolder(
        private val binding: ItemFighterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee, isExpanded: Boolean, onItemClick: (String) -> Unit) {
            binding.name.text = employee.name
            binding.rank.text = employee.rank
            binding.unitName.text = employee.currentUnitId ?: "Нет звена"
            binding.commanderIcon.visibility = if (employee.isCommander) View.VISIBLE else View.GONE

            // Переключение видимости характеристик
            binding.attributesLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Обработка нажатия на имя
            binding.name.setOnClickListener {
                onItemClick(employee.id) // Передаем только ID сотрудника
            }
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
    }
}