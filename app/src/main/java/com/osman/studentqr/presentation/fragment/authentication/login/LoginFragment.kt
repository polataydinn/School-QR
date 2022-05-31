package com.osman.studentqr.presentation.fragment.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.osman.studentqr.R
import com.osman.studentqr.common.LoadingDialog
import com.osman.studentqr.common.ifStringIsNumber
import com.osman.studentqr.common.isEmailValid
import com.osman.studentqr.databinding.FragmentLoginBinding
import com.osman.studentqr.presentation.activity.AuthenticationActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    private val viewModel: LoginViewModel by viewModels()
    private var loadingDialog: LoadingDialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog((activity as AuthenticationActivity))

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

        viewModel.isUserVerified.observe(viewLifecycleOwner) {
            if (FirebaseAuth.getInstance().currentUser?.email != null) {
                if (it) {
                    (activity as AuthenticationActivity).startMainActivity()
                } else {
                    val mail = FirebaseAuth.getInstance().currentUser?.email
                    Toast.makeText(
                        context,
                        mail + " adresine gönderilen doğrulama maili onaylanmadı!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog?.startLoadingDialog()
            } else {
                loadingDialog?.dismissDialog()
            }
        }

        viewModel.isLoginSuccess.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "Giriş Başarılı", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Giriş Başarısız", Toast.LENGTH_SHORT).show()
            }
        }


        binding.loginButtonLogin.setOnClickListener {
            if (binding.radioGroupLogin.checkedRadioButtonId == R.id.student_radio_button_login) {
                if (binding.loginEditTextEmail.text.toString().ifStringIsNumber()) {
                    viewModel.login(
                        binding.loginEditTextEmail.text.toString() + "@ogr.ksu.edu.tr",
                        binding.loginEditTextPassword.text.toString()
                    )
                } else {
                    Toast.makeText(activity, "Hatalı öğrenci numarası girdiniz", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                if (binding.loginEditTextEmail.text.toString().isEmailValid()) {
                    viewModel.login(
                        binding.loginEditTextEmail.text.toString(),
                        binding.loginEditTextPassword.text.toString()
                    )
                } else {
                    Toast.makeText(activity, "Hatalı mail adresi girdiniz", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}