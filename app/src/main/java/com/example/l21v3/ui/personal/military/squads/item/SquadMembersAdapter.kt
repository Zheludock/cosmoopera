package com.example.l21v3.ui.personal.military.squads.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.databinding.ItemSquadMemberBinding
import com.example.l21v3.model.Employee

class SquadMembersAdapter : ListAdapter<Employee, SquadMembersAdapter.ViewHolder>(
    EmployeeDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSquadMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemSquadMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee) {
            binding.apply {
                employeeName.text = employee.name
                employeeRank.text = employee.rank

                // Иконка командира
                commanderIcon.visibility = if (employee.isCommander) View.VISIBLE else View.GONE

                // Оружие в правой руке
                rightHandArm.text = employee.rightHandArm ?: "Нет"

                // Оружие в левой руке
                leftHandArm.text = employee.leftHandArm ?: "Нет"
                leftHandArm.visibility = if (employee.leftHandArm != null) View.VISIBLE else View.GONE
            }
        }
    }

    class EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Employee, newItem: Employee) = oldItem == newItem
    }
}