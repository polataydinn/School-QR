package com.osman.studentqr.presentation.fragment.new_lesson

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.common.getWeekNameByPosition
import com.osman.studentqr.data.Repository.FirebaseRepository
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.data.model.Teacher
import com.osman.studentqr.data.model.TeacherLesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewLessonViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {
    val isAddLessonSuccessful = MutableLiveData<Boolean>()
    val qrValue = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    fun addLesson(lessonName: String) {
        isLoading.value = true
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
            val listOfLessons = mutableListOf<Lesson>()
            for (item in 0..13){
                listOfLessons.add(Lesson(
                    lessonName = lessonName,
                    teacher = Teacher(teacherName, teacherMail),
                    lessonUUID = UUID.randomUUID().toString(),
                    listOfStudents = mutableListOf(),
                    isLessonOnline = false,
                    lessonWeek = item.getWeekNameByPosition(listOfWeeks())
                ))
            }

            val teacherLesson = TeacherLesson(
                lessonName = lessonName,
                listOfLessons = listOfLessons
            )
            repository.createNewLesson(teacherLesson) {
                isLoading.value = false
                isAddLessonSuccessful.value = it
            }
        }
    }

    private fun listOfWeeks() = listOf(
        "1. Hafta",
        "2. Hafta",
        "3. Hafta",
        "4. Hafta",
        "5. Hafta",
        "6. Hafta",
        "7. Hafta",
        "8. Hafta",
        "9. Hafta",
        "10. Hafta",
        "11. Hafta",
        "12. Hafta",
        "13. Hafta",
        "14. Hafta",
    )

    private fun createUUID(): String {
        return java.util.UUID.randomUUID().toString()
    }
}