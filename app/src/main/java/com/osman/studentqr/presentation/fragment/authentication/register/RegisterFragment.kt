package com.osman.studentqr.presentation.fragment.authentication.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.R
import com.osman.studentqr.databinding.FragmentRegisterBinding
import com.osman.studentqr.presentation.binding_adapter.BindingFragment

class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRegisterBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.radioGroup.check(R.id.student_radio_button)

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.student_radio_button -> {
                    binding.radioGroup.check(R.id.student_radio_button)
                    binding.emailEditText.hint = "Okul Numarası"
                    binding.emailEditText.inputType =
                        android.text.InputType.TYPE_CLASS_NUMBER
                }
                R.id.teacher_radio_button -> {
                    binding.radioGroup.check(R.id.teacher_radio_button)
                    binding.emailEditText.hint = "Öğretmen Mail Adresi"
                    binding.emailEditText.inputType =
                        android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                }
            }
        }
    }
}