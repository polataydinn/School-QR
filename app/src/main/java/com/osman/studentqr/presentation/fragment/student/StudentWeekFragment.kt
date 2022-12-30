package com.osman.studentqr.presentation.fragment.student

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.data.model.TeacherLesson
import com.osman.studentqr.databinding.FragmentStudentWeekBinding
import com.osman.studentqr.presentation.activity.MainActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import com.osman.studentqr.presentation.fragment.teacher.adapter.TeacherLessonsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentWeekFragment : BindingFragment<FragmentStudentWeekBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentStudentWeekBinding::inflate

    private val viewModel: StudentViewModel by viewModels()


    private val adaper: TeacherLessonsAdapter by lazy { TeacherLessonsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = (activity as MainActivity).teacherLesson
        item?.lessonName?.let { viewModel.getTotalNonattendance(it) }
        setRecyclerView(item)
        getObservers()
    }

    @SuppressLint("SetTextI18n")
    private fun getObservers() {
        viewModel.totalNonattendance.observe(viewLifecycleOwner) {
            if (it >= 4){
                binding.textView.text = "Devamsızlık Hakkınız kalmadı"
            } else {
                binding.textView.text = "Devamsızlık Hakkı : ${4 - it} Hafta"
            }
        }
    }

    private fun setRecyclerView(item: TeacherLesson?) {
        adaper.submitList(item?.listOfLessons)
        binding.studentWeekRecyclerView.adapter = adaper
    }

}