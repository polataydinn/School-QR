package com.osman.studentqr.presentation.fragment.teacher

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.data.Repository.FirebaseRepository
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.data.model.ReportData
import com.osman.studentqr.data.model.TeacherLesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>(true)
    val teacherLessonsList = MutableLiveData<List<TeacherLesson>>()
    val isEmpty = MutableLiveData<Boolean>(false)
    val currentPosition = MutableLiveData<Int>()

    init {
        getTeacherLessons()
    }

    suspend fun getReportDocument(lessonName: String) : List<ReportData>{
           return  repository.createLessonReport(lessonName)

    }

    private fun getTeacherLessons() {
        viewModelScope.launch {
            repository.getTeacherLessons{
                isLoading.value = false
                if (it.isEmpty()) {
                    isEmpty.value = true
                } else {
                    isEmpty.value = false
                    teacherLessonsList.value = it
                }
            }
        }
    }
}