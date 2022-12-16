package com.osman.studentqr.presentation.fragment.student

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.data.Repository.FirebaseRepository
import com.osman.studentqr.data.model.Lesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    val listOfLessons = MutableLiveData<List<Lesson>>()
    val isEmpty = MutableLiveData<Boolean>()

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
}