// AddBottomSheetDialog.kt
package com.example.studyfy.modules.bottomBar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studyfy.databinding.BottomSheetAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddBottomSheetDialog(
    private val onOptionSelected: () -> Unit,
    private val onOption2Selected: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddBinding.inflate(inflater, container, false)

        binding.option1.setOnClickListener {
            onOptionSelected()
            dismiss()
        }

        binding.option2.setOnClickListener {
            onOption2Selected()
            dismiss()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
