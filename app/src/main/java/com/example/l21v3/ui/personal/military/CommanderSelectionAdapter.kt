package com.example.l21v3.ui.personal.military

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.R
import com.example.l21v3.databinding.ItemEmployeeBinding
import com.example.l21v3.model.Employee

class CommanderSelectionAdapter(
    private val onMemberSelected: (Employee) -> Unit
) : ListAdapter<Employee, CommanderSelectionAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = getItem(position)
        holder.binding.name.text = employee.name
        holder.binding.rank.text = employee.rank
        holder.binding.leftHandIcon.setImageResource(getWeaponIconRes(employee.leftHandArm))
        holder.binding.rightHandIcon.setImageResource(getWeaponIconRes(employee.rightHandArm))
        holder.binding.root.setOnClickListener { onMemberSelected(employee) }
        holder.binding.actionIcon.visibility = View.GONE
    }

    class DiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Employee, newItem: Employee) = oldItem == newItem
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