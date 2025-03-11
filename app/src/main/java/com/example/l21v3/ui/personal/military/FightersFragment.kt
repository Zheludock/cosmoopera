package com.example.l21v3.ui.personal.military

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.l21v3.R
import com.example.l21v3.databinding.FragmentFightersBinding
import com.google.android.material.snackbar.Snackbar
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
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Наблюдение за списком бойцов
        viewModel.fighters.observe(viewLifecycleOwner) { fighters ->
            adapter.submitList(fighters)
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            // Покажите сообщение пользователю (например, через Snackbar)
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }

        // Наблюдение за состоянием раскрытых элементов
        viewModel.expandedItems.observe(viewLifecycleOwner) { expandedItems ->
            adapter.expandedItems = expandedItems
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}