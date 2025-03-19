package com.example.l21v3.ui.personal.military

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.databinding.ItemSquadBinding
import com.example.l21v3.databinding.ItemSubunitBinding
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import com.example.l21v3.model.Subunit

class SubunitAdapter(
    private val onEmployeeClick: (Employee) -> Unit,
    private val onSquadClick: (Squad) -> Unit
) : ListAdapter<Subunit, RecyclerView.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_EMPLOYEE -> {
                val binding = ItemSubunitBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                EmployeeViewHolder(binding, onEmployeeClick)
            }
            TYPE_SQUAD -> {
                val binding = ItemSquadBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SquadViewHolder(binding, onSquadClick)
            }
            else -> throw IllegalArgumentException("Неизвестный тип ViewHolder")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val subunit = getItem(position)) {
            is Subunit.EmployeeSubunit -> (holder as EmployeeViewHolder).bind(subunit.employee)
            is Subunit.SquadSubunit -> (holder as SquadViewHolder).bind(subunit.squad)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Subunit.EmployeeSubunit -> TYPE_EMPLOYEE
            is Subunit.SquadSubunit -> TYPE_SQUAD
        }
    }

    companion object {
        private const val TYPE_EMPLOYEE = 1
        private const val TYPE_SQUAD = 2

        private val DiffCallback = object : DiffUtil.ItemCallback<Subunit>() {
            override fun areItemsTheSame(oldItem: Subunit, newItem: Subunit) = when {
                oldItem is Subunit.EmployeeSubunit && newItem is Subunit.EmployeeSubunit ->
                    oldItem.employee.id == newItem.employee.id
                oldItem is Subunit.SquadSubunit && newItem is Subunit.SquadSubunit ->
                    oldItem.squad.id == newItem.squad.id
                else -> false
            }

            override fun areContentsTheSame(oldItem: Subunit, newItem: Subunit) = oldItem == newItem
        }
    }

    class EmployeeViewHolder(
        private val binding: ItemSubunitBinding,
        private val onEmployeeClick: (Employee) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee) {
            binding.subunitName.text = employee.name
            binding.root.setOnClickListener {
                onEmployeeClick(employee)
            }
        }
    }

    class SquadViewHolder(
        private val binding: ItemSquadBinding,
        private val onSquadClick: (Squad) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(squad: Squad) {
            binding.squadName.text = squad.name
            binding.root.setOnClickListener {
                onSquadClick(squad)
            }
        }
    }
}