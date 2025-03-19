package com.example.l21v3.ui.personal.military

import SquadsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.R
import com.example.l21v3.databinding.FragmentSquadsBinding
import com.example.l21v3.model.Squad
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SquadsFragment : Fragment() {

    private var _binding: FragmentSquadsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SquadsViewModel by viewModels()

    private var dialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSquadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Передаем функцию showAddSubunitDialog в адаптер
        val adapter = SquadsAdapter(
            onSquadClick = { squad ->
                viewModel.toggleSquadExpansion(squad.id)
            },
            showAddSubunitDialog = { squad ->
                showAddSubunitDialog(squad) // Передаем функцию
            }
        )

        binding.squadsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.squadsRecyclerView.adapter = adapter

        // Наблюдение за списком отрядов
        viewModel.squads.observe(viewLifecycleOwner) { squads ->
            adapter.submitList(squads)
        }

        // Нажатие на кнопку "Создать отряд"
        binding.createSquadButton.setOnClickListener {
            showCreateSquadDialog()
        }
    }

    private fun showCreateSquadDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_squad, null)

        val dialogSquadName = dialogView.findViewById<EditText>(R.id.dialogSquadName)
        val dialogSquadType = dialogView.findViewById<Spinner>(R.id.dialogSquadType)

        // Заполняем Spinner вариантами типов отряда
        val squadTypes = arrayOf("Звено", "Взвод", "Рота") // Пример данных
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, squadTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogSquadType.adapter = adapter

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Создать отряд")
            .setView(dialogView)
            .setPositiveButton("Создать") { _, _ ->
                val name = dialogSquadName.text.toString()
                val type = dialogSquadType.selectedItem as String
                viewModel.createSquad(name, type)
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    private fun showAddSubunitDialog(squad: Squad) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_subunit, null)

        val dialogRecyclerView = dialogView.findViewById<RecyclerView>(R.id.dialogRecyclerView)
        val adapter = SubunitAdapter(
            onEmployeeClick = { employee ->
                viewModel.addEmployeeToSquad(squad.id, employee.id)
            },
            onSquadClick = { subunit ->
                viewModel.addSubunitToSquad(squad.id, subunit.id)
            }
        )

        dialogRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        dialogRecyclerView.adapter = adapter

        dialog = AlertDialog.Builder(requireContext())
            .setTitle("Добавить подотряд")
            .setView(dialogView)
            .setNegativeButton("Отмена", null)
            .create()

        // Загрузка данных
        viewModel.loadFreeSubunits(squad.type)

        // Наблюдение за freeSubunits
        viewModel.freeSubunits.observe(viewLifecycleOwner) { subunits ->
            if (subunits.isEmpty()) {
                Toast.makeText(requireContext(), "Нечего добавить", Toast.LENGTH_SHORT).show()
                dialog?.dismiss() // Закрываем диалог, если список пуст
            } else {
                adapter.submitList(subunits) // Обновляем список в адаптере
            }
        }

        dialog?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}