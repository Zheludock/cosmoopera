package com.example.l21v3.ui.personal.military

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.l21v3.R
import com.example.l21v3.databinding.FragmentFightersBinding
import com.example.l21v3.model.Weapons
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FightersFragment : Fragment(R.layout.fragment_fighters) {

    private val viewModel: FightersViewModel by viewModels()

    private var _binding: FragmentFightersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFightersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация RecyclerView
        val adapter = FightersAdapter(
            expandedItems = viewModel.expandedItems.value ?: emptySet(),
            onItemClick = { employeeId ->
                viewModel.toggleAttributes(employeeId)
            },
            onActionClick = { action, employee ->
                when (action) {
                    FightersAdapter.RIGHT_HAND -> {
                        showWeaponSelectionDialog(employee.rightHandArm) { selectedWeapon ->
                            viewModel.giveRightHandAmmo(employee, selectedWeapon)
                        }
                    }
                    FightersAdapter.DELETE_ACTION -> viewModel.deleteEmployee(employee)
                    FightersAdapter.LEFT_HAND -> {
                        showLeftHandWeaponSelectionDialog(employee.leftHandArm) { selectedWeapon ->
                            viewModel.giveLeftHandAmmo(employee, selectedWeapon)
                        }
                    }
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Наблюдение за списком бойцов
        viewModel.fighters.observe(viewLifecycleOwner) { fighters ->
            adapter.submitList(fighters)
        }

        // Наблюдение за состоянием раскрытых элементов
        viewModel.expandedItems.observe(viewLifecycleOwner) { expandedItems ->
            adapter.expandedItems = expandedItems
            adapter.notifyDataSetChanged()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}