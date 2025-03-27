package com.example.l21v3.ui.personal.scientists

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.R
import com.example.l21v3.databinding.FragmentScientistsBinding
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import com.example.l21v3.ui.personal.military.CommanderSelectionAdapter
import com.example.l21v3.ui.personal.military.Section
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScientistsFragment : Fragment() {

    private var _binding: FragmentScientistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScientistsViewModel by viewModels()
    private lateinit var adapter: ScientistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScientistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ScientistsAdapter(
            onHeaderClick = { sectionId -> adapter.toggleSection(sectionId) },
            onItemClick = { employeeId -> adapter.toggleItem(employeeId) },
            onHeaderAction = { section, view -> showHeaderActionMenu(section, view) },
            onEmployeeAction = { employee, view -> showEmployeeActionMenu(employee, view) }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ScientistsFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.sections.observe(viewLifecycleOwner) { sections ->
            adapter.submitSections(sections)
        }
        viewModel.selectCommanderEvent.observe(viewLifecycleOwner) { event ->
            event?.let { showCommanderSelectionDialog(it.squad, it.members) }
        }
    }

    private fun showHeaderActionMenu(section: Section, anchor: View) {
        val popup = PopupMenu(requireContext(), anchor).apply {
            menuInflater.inflate(R.menu.menu_engineer_department_actions, menu)
        }
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_assign_commander -> {
                    viewModel.showSelectCommanderDialog(section.id, section.employees)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showEmployeeActionMenu(employee: Employee, anchor: View) {
        val popup = PopupMenu(requireContext(), anchor).apply {
            menuInflater.inflate(R.menu.menu_engineer_employee_actions, menu)
        }
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_transfer -> {
                    showTransferDialog(employee)
                    true
                }
                R.id.delete -> {
                    viewModel.deleteEmployee(employee)
                    true
                }
                R.id.promote -> {
                    viewModel.promoteEmployee(employee)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showCommanderSelectionDialog(squad: Squad, members: List<Employee>) {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_select_commander)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val adapter = CommanderSelectionAdapter { employee ->
            viewModel.assignCommander(squad, employee)
            dialog.dismiss()
        }

        dialog.findViewById<RecyclerView>(R.id.membersList).apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        dialog.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        adapter.submitList(members)
        dialog.show()
    }

    private fun showTransferDialog(employee: Employee) {
        viewModel.getAvailableSquads(employee.currentSquadId).observe(viewLifecycleOwner) { squads ->
            val options = mutableListOf<String>().apply {
                add("Без отряда") // Добавляем опцию исключения из отряда
                addAll(squads.map { it.name })
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Перевод сотрудника ${employee.name}")
                .setItems(options.toTypedArray()) { _, which ->
                    when (which) {
                        0 -> viewModel.transferEmployee(employee, null) // Без отряда
                        else -> {
                            val selectedSquad = squads[which - 1] // Сдвиг из-за новой опции
                            viewModel.transferEmployee(employee, selectedSquad.id)
                        }
                    }
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
}