package com.osman.studentqr.presentation.fragment.lesson_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.data.Repository.FirebaseRepository
import com.osman.studentqr.data.model.Student
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    val isRemoved = MutableLiveData<Boolean>()
    val studentsList = MutableLiveData<List<Student>>()
    val isEmpty = MutableLiveData<Boolean>()

    fun removeLesson(uuid: String) {
        viewModelScope.launch {
            repository.removeLesson(uuid) {
                isRemoved.value = it
            }
        }
    }

    fun getListOfStudents(uuid: String) {
        viewModelScope.launch {
            repository.getListOfStudents(uuid) { listOfStudents ->
                if (listOfStudents.isEmpty()) {
                    isEmpty.value = true
                } else {
                    isEmpty.value = false
                    studentsList.value = listOfStudents
                }
            }
        }
    }
}
