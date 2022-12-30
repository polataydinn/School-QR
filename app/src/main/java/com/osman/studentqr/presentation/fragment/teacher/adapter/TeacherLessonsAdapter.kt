package com.osman.studentqr.presentation.fragment.teacher.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.osman.studentqr.R
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.databinding.RowLessonsItemBinding

class TeacherLessonsAdapter : ListAdapter<Lesson, TeacherLessonsViewHolder>(COINS_DIFF_CALLBACK) {

    var onItemClick: ((Lesson, Int) -> Unit)? = null


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
        if (getItem(position) != null){
            holder.bind(getItem(position), onItemClick, position)
        }
    }

    companion object {
        val COINS_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Lesson>() {
            override fun areItemsTheSame(
                oldItem: Lesson,
                newItem: Lesson
            ): Boolean {
                return oldItem.lessonWeek == newItem.lessonWeek
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
    fun bind(lesson: Lesson, onItemClick: ((Lesson, Int) -> Unit)?, position: Int) {
        binding.lessonTeacherName.text = lesson.lessonWeek
        binding.lessonNameTextview.text = lesson.lessonName
        val studentsCount = "Katılan Öğrenci Sayısı : " + lesson.listOfStudents?.size.toString()
        binding.lessonAttemptCount.text = studentsCount
        if (lesson.isLessonOnline == false){
            binding.root.background = (ContextCompat.getDrawable(binding.root.context, R.drawable.custom_lesson_item_gray_background))
        }else{
            binding.root.background = (ContextCompat.getDrawable(binding.root.context, R.drawable.custom_lesson_item_background))
        }

        binding.root.setOnClickListener {
            onItemClick?.invoke(lesson, position)
        }
    }
}
