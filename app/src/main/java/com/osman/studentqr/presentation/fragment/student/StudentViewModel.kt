package com.osman.studentqr.presentation.fragment.student

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.data.Repository.FirebaseRepository
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.data.model.TeacherLesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    val listOfLessons = MutableLiveData<List<TeacherLesson>>()
    val isEmpty = MutableLiveData<Boolean>()

    val totalNonattendance = MutableLiveData<Int>()

    fun listOfLessonsStudentAttempted(lesson: Lesson){
        viewModelScope.launch {
            repository.listOfLessonsStudentAttempted(lesson){
                if (it.isEmpty()){
                    isEmpty.value = true
                }else{
                    isEmpty.value = false
                    listOfLessons.value = it
                }
            }
        }
    }

    fun getTotalNonattendance(lessonName: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUsersAllLesson(lessonName){
                totalNonattendance.postValue(it)
            }
        }
    }
}