package com.osman.studentqr.presentation.fragment.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.databinding.FragmentTeacherLessonListBinding
import com.osman.studentqr.presentation.activity.MainActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import com.osman.studentqr.presentation.fragment.lesson_detail.LessonDetailFragment
import com.osman.studentqr.presentation.fragment.teacher.adapter.TeacherLessonsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        viewModel.teacherLessonsList.observe(viewLifecycleOwner) {
            val position = (activity as MainActivity).position
            adapter.submitList(position?.let { it1 -> it[it1].listOfLessons })
        }

        adapter.onItemClick = { mLesson, position ->
            (activity as MainActivity).apply {
                lesson = mLesson
                lessonPosition = position
                loadFragment(LessonDetailFragment())
            }
        }

        binding.teacherLessonCreateDocumentButton.setOnClickListener {
            val position = (activity as MainActivity).position
            val lessonKey =
                position?.let { it1 -> viewModel.teacherLessonsList.value?.get(it1)?.teacherKey }
            val lessonName =
                position?.let { it1 -> viewModel.teacherLessonsList.value?.get(it1)?.lessonName }
            lifecycleScope.launch(Dispatchers.IO) {
                val reportList =
                    lessonKey?.let { mLessonName -> viewModel.getReportDocument(mLessonName) }
                (activity as MainActivity).apply {
                    reportList?.let { mReportList ->
                        lessonName?.let { mLessonName ->
                            createPdf(
                                mReportList,
                                mLessonName
                            )
                        }
                    }
                }
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