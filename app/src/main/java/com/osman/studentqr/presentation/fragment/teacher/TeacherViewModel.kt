package com.osman.studentqr.presentation.fragment.teacher

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.data.Repository.FirebaseRepository
import com.osman.studentqr.data.model.Lesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>(true)
    val teacherLessonsList = MutableLiveData<List<Lesson>>()
    val isEmpty = MutableLiveData<Boolean>(false)

    init {
        getTeacherLessons()
    }

    fun getTeacherLessons() {
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