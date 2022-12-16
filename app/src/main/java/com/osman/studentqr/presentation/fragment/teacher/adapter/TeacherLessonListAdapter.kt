package com.osman.studentqr.presentation.fragment.teacher.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.osman.studentqr.data.model.TeacherLesson
import com.osman.studentqr.databinding.CustomLessonItemBinding

class TeacherLessonListAdapter :
    ListAdapter<TeacherLesson, TeacherLessonViewHolder>(TEACHER_LESSON_CALLBACK) {
    var onItemClick: ((TeacherLesson, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherLessonViewHolder {
        return TeacherLessonViewHolder(
            CustomLessonItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TeacherLessonViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, position)
    }

    companion object {
        val TEACHER_LESSON_CALLBACK = object : DiffUtil.ItemCallback<TeacherLesson>() {
            override fun areItemsTheSame(oldItem: TeacherLesson, newItem: TeacherLesson): Boolean {
                return oldItem.lessonName == newItem.lessonName
            }

            override fun areContentsTheSame(
                oldItem: TeacherLesson,
                newItem: TeacherLesson
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}

class TeacherLessonViewHolder(private val binding: CustomLessonItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: TeacherLesson?, onItemClick: ((TeacherLesson, Int) -> Unit)?, position: Int) {
        binding.lessonNameTextview.text = item?.lessonName
        binding.root.setOnClickListener {
            item?.let { teacherLesson -> onItemClick?.invoke(teacherLesson, position) }
        }
    }

}
