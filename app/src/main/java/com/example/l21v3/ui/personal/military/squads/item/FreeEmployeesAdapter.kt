package com.example.l21v3.ui.personal.military.squads.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.databinding.ItemEmployeeCheckboxBinding
import com.example.l21v3.model.Employee

class FreeEmployeesAdapter : ListAdapter<Employee, FreeEmployeesAdapter.ViewHolder>(
    EmployeeDiffCallback()
) {

    private val selectedIds = mutableSetOf<String>()

    inner class ViewHolder(private val binding: ItemEmployeeCheckboxBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee) {
            with(binding) {
                name.text = employee.name
                details.text = buildString {
                    append("Звание: ${employee.rank}")
                    append(", Правая рука: ${employee.rightHandArm ?: "нет"}")
                    if (employee.leftHandArm != null) {
                        append(", Левая рука: ${employee.leftHandArm}")
                    }
                }

                checkbox.isChecked = selectedIds.contains(employee.id)

                root.setOnClickListener {
                    checkbox.isChecked = !checkbox.isChecked
                    toggleSelection(employee.id)
                }

                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    toggleSelection(employee.id, isChecked)
                }
            }
        }

        private fun toggleSelection(id: String, isChecked: Boolean? = null) {
            val checked = isChecked ?: !selectedIds.contains(id)
            if (checked) {
                selectedIds.add(id)
            } else {
                selectedIds.remove(id)
            }
            notifyItemChanged(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmployeeCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedEmployees(): List<Employee> {
        return currentList.filter { selectedIds.contains(it.id) }
    }

    class EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }
}