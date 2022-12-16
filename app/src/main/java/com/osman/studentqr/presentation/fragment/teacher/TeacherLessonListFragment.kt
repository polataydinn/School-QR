package com.osman.studentqr.presentation.fragment.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.databinding.FragmentTeacherLessonListBinding
import com.osman.studentqr.presentation.activity.MainActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import com.osman.studentqr.presentation.fragment.lesson_detail.LessonDetailFragment
import com.osman.studentqr.presentation.fragment.teacher.adapter.TeacherLessonsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherLessonListFragment : BindingFragment<FragmentTeacherLessonListBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentTeacherLessonListBinding::inflate

    private val adapter: TeacherLessonsAdapter by lazy { TeacherLessonsAdapter() }

    private val viewModel: TeacherViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        observeLoadingAndEmpty()
        viewModel.teacherLessonsList.observe(viewLifecycleOwner){
            adapter.submitList(viewModel.currentPosition.value?.let { position -> it[position].listOfLessons })
        }

        adapter.onItemClick = { mLesson, position ->
            (activity as MainActivity).apply {
                lesson = mLesson
                lessonPosition = position
                loadFragment(LessonDetailFragment())
            }
        }

    }

    private fun observeLoadingAndEmpty() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.teacherWeekLoadingProgressBar.isVisible = it
        }
        viewModel.isEmpty.observe(viewLifecycleOwner) {
            binding.teacherWeekEmptyImage.isVisible = it
            if (it) {
                adapter.submitList(emptyList())
            }
        }
    }

    private fun setRecyclerView() {
        binding.teacherWeekLessonsRecyclerView.adapter = adapter
    }

}