package com.example.l21v3.ui.personal.military.squads.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.databinding.ItemSquadBinding
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad

class SquadAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onLoadMembers: (String) -> Unit,
    private val onSquadClick: (String) -> Unit,
    private val onAddClick: (Squad) -> Unit
) : ListAdapter<Squad, SquadAdapter.ViewHolder>(SquadDiffCallback()) {

    private val membersAdapters = mutableMapOf<String, SquadMembersAdapter>()
    private var expandedSquadIds = emptySet<String>()

    inner class ViewHolder(private val binding: ItemSquadBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(squad: Squad) {
            binding.apply {
                squadName.text = squad.name
                squadSize.text = "${squad.currentSize}/${Squad.MAXSIZE}"

                val membersAdapter = membersAdapters.getOrPut(squad.id) {
                    SquadMembersAdapter().also {
                        membersRecyclerView.adapter = it
                        membersRecyclerView.layoutManager = LinearLayoutManager(root.context)
                    }
                }

                val isExpanded = expandedSquadIds.contains(squad.id)
                membersRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
                root.setOnClickListener { onSquadClick(squad.id) }

                if (isExpanded) {
                    onLoadMembers(squad.id)
                }

                ivAddMember.visibility = if (squad.currentSize < Squad.MAXSIZE) View.VISIBLE else View.GONE
                ivAddMember.setOnClickListener { onAddClick(squad) }
            }
        }
    }

    fun updateExpandedItems(expandedIds: Set<String>) {
        expandedSquadIds = expandedIds
        notifyDataSetChanged()
    }

    fun updateMembers(squadId: String, members: List<Employee>) {
        membersAdapters[squadId]?.submitList(members)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSquadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SquadDiffCallback : DiffUtil.ItemCallback<Squad>() {
    override fun areItemsTheSame(oldItem: Squad, newItem: Squad) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Squad, newItem: Squad) = oldItem == newItem
}