package com.example.l21v3.ui.personal.military

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.R
import com.example.l21v3.databinding.FragmentMilitaryBinding
import com.example.l21v3.model.Employee
import com.example.l21v3.model.Squad
import com.example.l21v3.model.Weapons
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MilitaryFragment : Fragment() {

    private var _binding: FragmentMilitaryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MilitaryViewModel by viewModels()
    private lateinit var adapter: MilitaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMilitaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        adapter = MilitaryAdapter(
            onHeaderClick = { sectionId ->
                adapter.toggleSection(sectionId)
            },
            onItemClick = { employeeId ->
                adapter.toggleItem(employeeId)
            },
            onHeaderAction = { section, view ->
                showHeaderActionMenu(section, view)
            },
            onEmployeeAction = { employee, view ->
                showEmployeeActionMenu(employee, view)
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MilitaryFragment.adapter
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

    private fun setupAddButton() {
        binding.fabAddSquad.setOnClickListener {
            showCreateSquadDialog()
        }
    }

    private fun showCreateSquadDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "Название отряда"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Создать отряд")
            .setView(editText)
            .setPositiveButton("Создать") { _, _ ->
                viewModel.createSquad(editText.text.toString().trim(), "Military")
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showHeaderActionMenu(section: Section, anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.menu_squad_actions, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_rename -> {
                    showRenameSquadDialog(section)
                    true
                }
                R.id.action_delete -> {
                    deleteSquad(section)
                    true
                }
                R.id.action_assign_commander -> {
                    viewModel.showSelectCommanderDialog(section.id)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showEmployeeActionMenu(employee: Employee, anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.menu_employye_actions, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.giveRightHandAmmo -> {
                    showWeaponSelectionDialog(employee.rightHandArm) { selectedWeapon ->
                        viewModel.giveRightHandAmmo(employee, selectedWeapon)
                    }
                    true
                }
                R.id.giveLeftHandAmmo -> {
                    showLeftHandWeaponSelectionDialog(employee.leftHandArm) { selectedWeapon ->
                        viewModel.giveLeftHandAmmo(employee, selectedWeapon)
                    }
                    true
                }
                R.id.action_transfer -> {
                    showTransferDialog(employee)
                    true
                }
                R.id.delete -> {
                    viewModel.deleteEmployee(employee)
                    true
                }
                else -> {
                    false
                }
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

    private fun showWeaponSelectionDialog(
        currentWeapon: String?,
        onWeaponSelected: (String) -> Unit
    ) {
        val weapons = Weapons.values().map { it.name }
        val currentIndex = weapons.indexOfFirst {
            it.equals(currentWeapon, ignoreCase = true)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите оружие для правой руки")
            .setSingleChoiceItems(weapons.toTypedArray(), currentIndex) { dialog, which ->
                val selectedWeapon = Weapons.values()[which].name
                onWeaponSelected(selectedWeapon)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showLeftHandWeaponSelectionDialog(
        currentWeapon: String?,
        onWeaponSelected: (String) -> Unit
    ) {
        val weapons = listOf("Pistolet", "Sword", "Grenade") // Ограниченный список
        val currentIndex = weapons.indexOfFirst {
            it.equals(currentWeapon, ignoreCase = true)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите оружие для левой руки")
            .setSingleChoiceItems(weapons.toTypedArray(), currentIndex) { dialog, which ->
                val selectedWeapon = weapons[which]
                onWeaponSelected(selectedWeapon)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
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

    private fun deleteSquad(section: Section) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление отряда")
            .setMessage("Вы уверены, что хотите удалить отряд ${section.title}?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteSquad(section.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showRenameSquadDialog(section: Section) {
        val editText = EditText(requireContext()).apply {
            setText(section.title)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Переименование отряда")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = editText.text.toString().trim()
                viewModel.renameSquad(section.id, newName)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}