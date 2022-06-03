package com.osman.studentqr.presentation.fragment.authentication.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.osman.studentqr.R
import com.osman.studentqr.common.LoadingDialog
import com.osman.studentqr.common.ifStringIsNumber
import com.osman.studentqr.databinding.FragmentRegisterBinding
import com.osman.studentqr.presentation.activity.AuthenticationActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRegisterBinding::inflate

    private val viewModel: RegisterViewModel by viewModels()
    private var loadingDialog: LoadingDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.radioGroup.check(R.id.student_radio_button)
        loadingDialog = LoadingDialog(activity as AuthenticationActivity)

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

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog?.startLoadingDialog()
            } else {
                loadingDialog?.dismissDialog()
            }
        }

        viewModel.isUserVerified.observe(viewLifecycleOwner) {
            val mail = Firebase.auth.currentUser?.email
            if (it) {
                Toast.makeText(context, "Kullanıcı başarıyla kayıt edildi", Toast.LENGTH_SHORT)
                    .show()
                if (mail != null && mail.contains("@ogr.ksu.edu.tr")) {
                    (activity as AuthenticationActivity).startMainActivity(true)
                } else {
                    (activity as AuthenticationActivity).startMainActivity(false)
                }
            } else {
                if (mail != null) {
                    Toast.makeText(
                        context,
                        mail + " adresine gönderilen doğrulama maili onaylanmadı!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.isRegisterSuccess.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "Kayıt Başarılı", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Kayıt Başarısız", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerButton.setOnClickListener {
            if (binding.emailEditText.text.isNullOrEmpty() || binding.passwordEditText.text.isNullOrEmpty()) {
                Toast.makeText(activity, "Boş alanları doldurunuz!", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.radioGroup.checkedRadioButtonId == R.id.student_radio_button) {
                    if (binding.emailEditText.text.toString().length != 11 &&
                        binding.emailEditText.text.toString().ifStringIsNumber()
                    ) {
                        binding.emailEditText.error = "Hatalı okul numarası girdiniz!"
                    } else {
                        viewModel.register(
                            binding.emailEditText.text.toString() + "@ogr.ksu.edu.tr",
                            binding.passwordEditText.text.toString(),
                            binding.nameEditText.text.toString(),
                            true
                        )
                    }
                } else {
                    if (binding.emailEditText.text.toString().contains("@")) {
                        viewModel.register(
                            binding.emailEditText.text.toString(),
                            binding.passwordEditText.text.toString(),
                            binding.nameEditText.text.toString(),
                            false
                        )
                    } else {
                        binding.emailEditText.error = "Hatalı mail adresi girdiniz!"
                    }
                }
            }
        }
    }
}