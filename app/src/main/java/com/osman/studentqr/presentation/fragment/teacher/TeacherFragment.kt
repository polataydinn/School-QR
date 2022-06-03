package com.osman.studentqr.presentation.fragment.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.databinding.FragmentTeacherBinding
import com.osman.studentqr.presentation.activity.MainActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import com.osman.studentqr.presentation.fragment.lesson_detail.LessonDetailFragment
import com.osman.studentqr.presentation.fragment.new_lesson.NewLessonFragment
import com.osman.studentqr.presentation.fragment.teacher.adapter.TeacherLessonsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherFragment : BindingFragment<FragmentTeacherBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentTeacherBinding::inflate

    private val viewModel: TeacherViewModel by viewModels()
    private val adapter: TeacherLessonsAdapter by lazy { TeacherLessonsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureAdapter()
        createNewLesson()
        observeLessons()
        observeLoadingAndEmpty()
    }

    private fun configureAdapter() {
        binding.teacherLessonsRecyclerView.adapter = adapter

        adapter.onItemClick = {
            (activity as MainActivity).apply {
                lesson = it
                loadFragment(LessonDetailFragment())
            }
        }
    }

    private fun observeLessons() {
        viewModel.teacherLessonsList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun observeLoadingAndEmpty() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.teacherLoadingProgressBar.isVisible = it
        }
        viewModel.isEmpty.observe(viewLifecycleOwner) {
            binding.teacherEmptyImage.isVisible = it
            if (it) {
                adapter.submitList(emptyList())
            }
        }
    }

    private fun createNewLesson() {
        binding.teacherCreateLessonButton.setOnClickListener {
            val fragment = NewLessonFragment()
            (activity as MainActivity).loadFragment(fragment)
        }
    }
}