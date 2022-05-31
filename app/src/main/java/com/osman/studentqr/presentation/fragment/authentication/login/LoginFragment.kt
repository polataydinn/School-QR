package com.osman.studentqr.presentation.fragment.authentication.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.R
import com.osman.studentqr.databinding.FragmentLoginBinding
import com.osman.studentqr.presentation.binding_adapter.BindingFragment

class LoginFragment : BindingFragment<FragmentLoginBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.radioGroupLogin.check(R.id.student_radio_button_login)

        binding.radioGroupLogin.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.student_radio_button_login -> {
                    binding.radioGroupLogin.check(R.id.student_radio_button_login)
                    binding.loginEditTextEmail.hint = "Okul Numarası"
                    binding.loginEditTextEmail.inputType =
                        android.text.InputType.TYPE_CLASS_NUMBER
                }
                R.id.teacher_radio_button_login -> {
                    binding.radioGroupLogin.check(R.id.teacher_radio_button_login)
                    binding.loginEditTextEmail.hint = "Öğretmen Mail Adresi"
                    binding.loginEditTextEmail.inputType =
                        android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                }
            }
        }
    }

}