package com.example.l21v3.ui.personal.military

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.l21v3.databinding.FragmentMilitaryBinding
import com.example.l21v3.ui.personal.ViewPagerAdapter
import com.example.l21v3.ui.personal.military.figters.FightersFragment
import com.example.l21v3.ui.personal.military.squads.SquadsFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MilitaryFragment : Fragment() {

    private var _binding: FragmentMilitaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MilitaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMilitaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Наблюдение за списком военных
        viewModel.militaryEmployees.observe(viewLifecycleOwner) {
            // Передайте данные в адаптер для подтаба "Бойцы"
        }

        // Наблюдение за сообщениями
        viewModel.message.observe(viewLifecycleOwner) { message ->
            // Покажите сообщение пользователю (например, через Snackbar)
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }

        // Настройка подтабов
        setupSubViewPager()
    }

    private fun setupSubViewPager() {
        val fragments = listOf(
            FightersFragment(),
            SquadsFragment(),
        )
        val titles = listOf("Бойцы", "Отряды")
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, fragments, titles)
        binding.subViewPager.adapter = adapter

        TabLayoutMediator(binding.subTabLayout, binding.subViewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}