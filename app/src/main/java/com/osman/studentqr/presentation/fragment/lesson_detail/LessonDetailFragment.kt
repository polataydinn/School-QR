package com.osman.studentqr.presentation.fragment.lesson_detail

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.R
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.databinding.FragmentLessonDetailBinding
import com.osman.studentqr.presentation.activity.MainActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import com.osman.studentqr.presentation.fragment.lesson_detail.adapter.LessonDetailAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LessonDetailFragment : BindingFragment<FragmentLessonDetailBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLessonDetailBinding::inflate

    private val viewModel: LessonDetailViewModel by viewModels()
    private var lesson: Lesson? = Lesson()
    private val adapter: LessonDetailAdapter by lazy { LessonDetailAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lesson = (activity as MainActivity).lesson
        lesson?.lessonUUID?.let { viewModel.getListOfStudents(it) }
        configureRecyclerView()
        uiEvents()
        observeStudentList()
        observeRemoveEvent()
    }

    private fun configureRecyclerView() {
        binding.lessonDetailAttemptsRecylerView.adapter = adapter
    }

    private fun uiEvents() {
        binding.lessonDetailTitle.text = lesson?.lessonName
        binding.lessonDetailAttempts.text =
            "Katılımcı Sayısı : " + lesson?.listOfStudents?.size.toString()

        binding.lessonDetailQrCodeButton.setOnClickListener {
            createQrCodeDialog()
        }

        binding.lessonDetailRemoveButton.setOnClickListener {
            createRemoveDialog()
        }
    }

    private fun observeRemoveEvent() {
        viewModel.isRemoved.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(activity, "Başarıyla Silindi", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).onBackPressed()
            } else {
                Toast.makeText(activity, "Silme İşlemi Başarısız", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeStudentList() {
        viewModel.studentsList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) {
            if (it) {
                adapter.submitList(emptyList())
            }
        }
    }

    private fun createRemoveDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Evet") { _, _ ->
            lesson?.lessonUUID?.let { viewModel.removeLesson(it) }
        }
        builder.setNegativeButton("Hayır") { _, _ -> }
        builder.setTitle(lesson?.lessonName)
        builder.setMessage("Dersi silmek istediğinize emin misiniz?")
        builder.create().show()
    }

    fun createQrCodeDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = (activity as MainActivity).layoutInflater
        val dialogView: View = inflater.inflate(R.layout.custom_qr_dialog, null)
        val qrImage = dialogView.findViewById<ImageView>(R.id.dialog_qr_image)

        val qrgEncoder = QRGEncoder(lesson?.lessonUUID, null, QRGContents.Type.TEXT, 1000)
        val qrBit: Bitmap = qrgEncoder.getBitmap()
        qrImage.setImageBitmap(qrBit)

        builder.setView(dialogView)
            .setPositiveButton("Tamam",
                DialogInterface.OnClickListener { dialog, which -> }).create().show()
    }
}