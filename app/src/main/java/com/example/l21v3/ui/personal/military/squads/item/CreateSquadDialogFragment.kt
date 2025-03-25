package com.example.l21v3.ui.personal.military.squads.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.l21v3.databinding.DialogCreateSquadBinding
import com.example.l21v3.ui.personal.military.squads.SquadsViewModel

class CreateSquadDialogFragment : DialogFragment() {

    private var _binding: DialogCreateSquadBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SquadsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateSquadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SquadsViewModel::class.java)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnCreate.setOnClickListener {
            val squadName = binding.etSquadName.text.toString().trim()
            if (squadName.isEmpty()) {
                showError("Введите название отряда")
                return@setOnClickListener
            }
            viewModel.createNewSquad(squadName)
            dismiss()
        }
    }

    private fun showError(message: String) {
        binding.etSquadName.error = message
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}