package com.example.l21v3.ui.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.l21v3.databinding.FragmentPersonalBinding
import com.example.l21v3.ui.personal.doctors.DoctorsFragment
import com.example.l21v3.ui.personal.engineers.EngineersFragment
import com.example.l21v3.ui.personal.military.MilitaryFragment
import com.example.l21v3.ui.personal.scientists.ScientistsFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PersonalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
    }

    private fun setupViewPager() {
        val fragments = listOf(
            MilitaryFragment(),
            ScientistsFragment(),
            EngineersFragment(),
            DoctorsFragment()
        )

        val titles = listOf("Военные", "Ученые", "Инженеры", "Врачи")

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, fragments, titles)

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}