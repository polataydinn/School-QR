package com.osman.studentqr.presentation.fragment.teacher.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.databinding.RowLessonsItemBinding

class TeacherLessonsAdapter : ListAdapter<Lesson, TeacherLessonsViewHolder>(COINS_DIFF_CALLBACK) {

    var onItemClick: ((Lesson) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherLessonsViewHolder {
        return TeacherLessonsViewHolder(
            RowLessonsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TeacherLessonsViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    companion object {
        val COINS_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Lesson>() {
            override fun areItemsTheSame(
                oldItem: Lesson,
                newItem: Lesson
            ): Boolean {
                return oldItem.lessonUUID == newItem.lessonUUID
            }

            override fun areContentsTheSame(
                oldItem: Lesson,
                newItem: Lesson
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class TeacherLessonsViewHolder(private val binding: RowLessonsItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(lesson: Lesson, onItemClick: ((Lesson) -> Unit)?) {
        binding.lessonTeacherName.text = lesson.teacher?.teacherName
        binding.lessonNameTextview.text = lesson.lessonName
        binding.lessonAttemptCount.text =
            "Katılan Öğrenci Sayısı : " + lesson.listOfStudents?.size.toString()

        binding.root.setOnClickListener {
            onItemClick?.invoke(lesson)
        }
    }
}
