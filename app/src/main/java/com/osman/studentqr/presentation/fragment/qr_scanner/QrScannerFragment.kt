package com.osman.studentqr.presentation.fragment.qr_scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.budiyev.android.codescanner.CodeScanner
import com.osman.studentqr.databinding.FragmentQrScannerBinding
import com.osman.studentqr.presentation.activity.MainActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrScannerFragment : BindingFragment<FragmentQrScannerBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentQrScannerBinding::inflate

    private var codeScanner: CodeScanner? = null
    private val viewModel: QrScannerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val codeScannerView = binding.qrScanner
        codeScanner = CodeScanner(activity as MainActivity, codeScannerView)

        viewModel.isSuccess.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(activity, "Ders başarılı bir şekilde eklendi.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(activity, "Ders eklenirken bir hata oluştu.", Toast.LENGTH_SHORT)
                    .show()
            }
            (activity as MainActivity).onBackPressed()
        }

        codeScanner?.setDecodeCallback { result ->
            viewModel.addStudentToLesson(result.text)
        }

    }

    override fun onResume() {
        super.onResume()
        codeScanner?.startPreview()
    }

}