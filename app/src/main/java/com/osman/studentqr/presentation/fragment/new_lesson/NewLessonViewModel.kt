package com.osman.studentqr.presentation.fragment.new_lesson

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.data.Repository.FirebaseRepository
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.data.model.Teacher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewLessonViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {
    val isAddLessonSuccessful = MutableLiveData<Boolean>()
    val qrValue = MutableLiveData<String>()

    fun addLesson(lessonName: String) {
        val qr = createUUID()
        qrValue.value = qr
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch {
                repository.getTeacherName { teacherName, teacherMail ->
                    createNewLesson(lessonName, teacherName, teacherMail, qr)
                }
            }
        }
    }

    private fun createNewLesson(
        lessonName: String,
        teacherName: String,
        teacherMail: String,
        qr: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val lesson = Lesson(
                lessonName = lessonName,
                teacher = Teacher(teacherName, teacherMail),
                lessonUUID = qr,
                listOfStudents = mutableListOf()
            )
            repository.createNewLesson(lesson) {
                isAddLessonSuccessful.value = it
            }
        }
    }

    private fun createUUID(): String {
        return java.util.UUID.randomUUID().toString()
    }
}