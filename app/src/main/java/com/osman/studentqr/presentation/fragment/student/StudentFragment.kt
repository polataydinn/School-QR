package com.osman.studentqr.presentation.fragment.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.databinding.FragmentStudentBinding
import com.osman.studentqr.presentation.activity.MainActivity
import com.osman.studentqr.presentation.binding_adapter.BindingFragment
import com.osman.studentqr.presentation.fragment.teacher.adapter.TeacherLessonsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentFragment : BindingFragment<FragmentStudentBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentStudentBinding::inflate

    private val viewModel: StudentViewModel by viewModels()
    private val adapter: TeacherLessonsAdapter by lazy { TeacherLessonsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        configureRecyclerView()
        observeListAndEmpty()
    }

    private fun configureRecyclerView() {
        binding.studentLessonListRecylerView.adapter = adapter
    }

    private fun observeListAndEmpty() {
        viewModel.isEmpty.observe(viewLifecycleOwner) {
            binding.studentEmptyImage.isVisible = it
            if (!it) {
                adapter.submitList(emptyList())
            }
        }

        viewModel.listOfLessons.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun listeners() {
        binding.studentLessonAddButton.setOnClickListener {
            (activity as MainActivity).openQrFragment()
        }
    }
}