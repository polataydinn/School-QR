package com.osman.studentqr.presentation.fragment.new_lesson

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.R
import com.osman.studentqr.common.LoadingDialog
import com.osman.studentqr.databinding.FragmentNewLessonBinding
import com.osman.studentqr.presentation.activity.MainActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewLessonFragment : BindingFragment<FragmentNewLessonBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentNewLessonBinding::inflate

    private val viewModel: NewLessonViewModel by viewModels()
    private var loadingDialog: LoadingDialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(activity as MainActivity)
        createNewLesson()

        viewModel.isAddLessonSuccessful.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(activity, "Ders Başarıyla Kaydedildi", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog?.startLoadingDialog()
            } else {
                loadingDialog?.dismissDialog()
            }
        }
    }

    private fun startQrScanner() {
        binding.createLessonButton.visibility = View.GONE
        binding.etLessonName.visibility = View.GONE
        val qrgEncoder = QRGEncoder(viewModel.qrValue.value, null, QRGContents.Type.TEXT, 1000)
        val qrBit: Bitmap = qrgEncoder.getBitmap()
        binding.lessonQrCode.setImageBitmap(qrBit)
    }

    private fun createNewLesson() {
        binding.createLessonButton.setOnClickListener {
            val lessonName = binding.etLessonName.text.toString()
            viewModel.addLesson(lessonName)
        }
    }
}