package com.example.l21v3.ui.personal.military.squads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.l21v3.databinding.FragmentSquadsBinding
import com.example.l21v3.model.Squad
import com.example.l21v3.ui.personal.military.squads.item.AddToSquadDialogFragment
import com.example.l21v3.ui.personal.military.squads.item.CreateSquadDialogFragment
import com.example.l21v3.ui.personal.military.squads.item.SquadAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SquadsFragment : Fragment() {

    private var _binding: FragmentSquadsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SquadAdapter

    private val viewModel: SquadsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSquadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = SquadAdapter(
            lifecycleOwner = viewLifecycleOwner,
            onLoadMembers = { squadId -> viewModel.loadSquadMembers(squadId) },
            onSquadClick = { squadId -> viewModel.toggleSquadExpansion(squadId) },
            onAddClick = { squad -> showAddMembersDialog(squad) }
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewModel.squads.observe(viewLifecycleOwner) { squads ->
            adapter.submitList(squads)
        }

        viewModel.expandedSquadIds.observe(viewLifecycleOwner) { expandedIds ->
            adapter.updateExpandedItems(expandedIds)
        }

        viewModel.squadMembers.observe(viewLifecycleOwner) { membersMap ->
            membersMap?.forEach { (squadId, membersList) ->
                adapter.updateMembers(squadId, membersList)
            }
        }
    }

    private fun setupFab() {
        binding.fabAddSquad.setOnClickListener {
            showCreateSquadDialog()
        }
    }

    private fun showCreateSquadDialog() {
        CreateSquadDialogFragment().apply {
            show(childFragmentManager, "CreateSquadDialog")
        }
    }

    private fun showAddMembersDialog(squad: Squad) {
        val maxMembers = Squad.MAXSIZE - squad.currentSize
        if (maxMembers > 0) {
            AddToSquadDialogFragment.newInstance(squad.id, maxMembers).apply {
                show(childFragmentManager, "AddToSquadDialog")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}