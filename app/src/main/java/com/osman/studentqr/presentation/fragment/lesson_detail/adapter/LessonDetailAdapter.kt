package com.osman.studentqr.presentation.fragment.lesson_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.osman.studentqr.data.model.Student
import com.osman.studentqr.databinding.RowLessonDetailItemBinding

class LessonDetailAdapter : ListAdapter<Student, LessonDetailViewHolder>(COINS_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonDetailViewHolder {
        return LessonDetailViewHolder(
            RowLessonDetailItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LessonDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val COINS_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Student>() {
            override fun areItemsTheSame(
                oldItem: Student,
                newItem: Student
            ): Boolean {
                return oldItem.studentNumber == newItem.studentNumber
            }

            override fun areContentsTheSame(
                oldItem: Student,
                newItem: Student
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class LessonDetailViewHolder(private val binding: RowLessonDetailItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(student: Student?) {
        binding.studentNameTextview.text = student?.studentName
    }

}
