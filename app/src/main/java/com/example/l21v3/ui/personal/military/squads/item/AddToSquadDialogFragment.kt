package com.example.l21v3.ui.personal.military.squads.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.l21v3.databinding.DialogAddToSquadBinding
import com.example.l21v3.ui.personal.military.squads.SquadsViewModel

class AddToSquadDialogFragment : DialogFragment() {
    private lateinit var binding: DialogAddToSquadBinding
    private lateinit var viewModel: SquadsViewModel
    private lateinit var adapter: FreeEmployeesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddToSquadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SquadsViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupButton()
    }

    private fun setupRecyclerView() {
        adapter = FreeEmployeesAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AddToSquadDialogFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.freeEmployees.observe(viewLifecycleOwner) { employees ->
            adapter.submitList(employees)
        }
    }

    private fun setupButton() {
        binding.btnConfirm.setOnClickListener {
            val selected = adapter.getSelectedEmployees()
            if (selected.isEmpty()) {
                Toast.makeText(context, "Выберите бойцов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val squadId = arguments?.getString("squad_id") ?: return@setOnClickListener
            viewModel.addMembersToSquad(squadId, selected.map { it.id })
            dismiss()
        }
    }

    companion object {
        fun newInstance(squadId: String, maxSelection: Int) = AddToSquadDialogFragment().apply {
            arguments = Bundle().apply {
                putString("squad_id", squadId)
                putInt("max_selection", maxSelection)
            }
        }
    }
}