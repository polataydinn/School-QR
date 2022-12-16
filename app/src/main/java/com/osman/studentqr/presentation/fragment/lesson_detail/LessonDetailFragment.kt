package com.osman.studentqr.presentation.fragment.lesson_detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
    private var dialogView: View? = null
    private var builder: AlertDialog.Builder? = null
    private var timerTextView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialog()
        lesson = (activity as MainActivity).lesson
        lesson?.let {
            viewModel.lesson = it
        }
        lesson?.lessonUUID?.let { viewModel.getListOfStudents(it) }
        configureRecyclerView()
        uiEvents()
        observeStudentList()
        observeRemoveEvent()
        observeQrStatus()
    }

    private fun setDialog() {
        builder = AlertDialog.Builder(activity)
        val inflater = (activity as MainActivity).layoutInflater
        dialogView = inflater.inflate(R.layout.custom_qr_dialog, null)
        timerTextView = dialogView!!.findViewById(R.id.dialog_timer_text)
    }

    @SuppressLint("SetTextI18n")
    private fun observeQrStatus() {
        viewModel.randomLessonUUID.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()){
                (activity as MainActivity).lesson?.lessonUUID = it
            }
            if (dialogView?.isShown == true) {
                updateDialog()
            }
        }
        viewModel.setTimer.observe(viewLifecycleOwner) {
            if (dialogView?.isShown == true) {
                timerTextView?.text = "$it saniye"
            }
        }
    }

    private fun configureRecyclerView() {
        binding.lessonDetailAttemptsRecylerView.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun uiEvents() {
        binding.lessonDetailTitle.text = lesson?.lessonName
        binding.lessonDetailAttempts.text =
            "Katılımcı Sayısı : " + lesson?.listOfStudents?.size.toString()
        binding.lessonDetailMakeLessonOnline.isChecked = lesson?.isLessonOnline ?: false
        binding.lessonDetailQrCodeButton.setOnClickListener {
            createQrCodeDialog()
        }

        binding.lessonDetailRemoveButton.setOnClickListener {
            createRemoveDialog()
        }
        binding.lessonDetailMakeLessonOnline.setOnCheckedChangeListener { _, isChecked ->
            val position = (activity as MainActivity).lessonPosition
            viewModel.setOnlineStatus(lesson, isChecked, position)
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

    private fun createQrCodeDialog() {
        if (dialogView == null) {
            return
        }
        viewModel.timer.cancel()
        viewModel.startTimer()
        val qrImage = dialogView!!.findViewById<ImageView>(R.id.dialog_qr_image)
        var qr: String? = ""
        qr = if (viewModel.randomLessonUUID.value?.isNotEmpty() == true) {
            viewModel.randomLessonUUID.value
        } else {
            lesson?.lessonUUID
        }
        val qrgEncoder = QRGEncoder(qr, null, QRGContents.Type.TEXT, 1000)
        if (qrgEncoder.bitmap == null) {
            return
        }
        val qrBit: Bitmap = qrgEncoder.bitmap
        qrImage.setImageBitmap(qrBit)
        if (dialogView!!.parent != null){
            (dialogView!!.parent as ViewGroup).removeView(dialogView)
        }
        builder?.setView(dialogView)
            ?.setPositiveButton("Tamam",
                DialogInterface.OnClickListener { dialog, which -> })?.create()?.show()
    }

    private fun updateDialog() {
        val qrImage = dialogView!!.findViewById<ImageView>(R.id.dialog_qr_image)
        var qr: String? = ""
        qr = if (viewModel.randomLessonUUID.value?.isNotEmpty() == true) {
            viewModel.randomLessonUUID.value
        } else {
            lesson?.lessonUUID
        }
        val qrgEncoder = QRGEncoder(qr, null, QRGContents.Type.TEXT, 1000)
        if (qrgEncoder.bitmap == null) {
            return
        }
        val qrBit: Bitmap = qrgEncoder.bitmap
        qrImage.setImageBitmap(qrBit)
    }
}