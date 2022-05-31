package com.osman.studentqr.presentation.fragment.authentication.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.data.Repository.FirebaseRepository
import com.osman.studentqr.data.model.Student
import com.osman.studentqr.data.model.Teacher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>(false)
    val isRegisterSuccess = MutableLiveData<Boolean>()

    val isUserVerified = MutableLiveData<Boolean>(false)

    fun checkIfUserVerified() {
        viewModelScope.launch {
            firebaseRepository.checkIfVerified {
                isUserVerified.value = it
            }
        }
    }

    fun register(email: String, password: String, fullName: String, isStudent: Boolean) {
        isLoading.value = true
        if (isStudent) {
            viewModelScope.launch {
                firebaseRepository.register(email, password) {
                    val student = Student(email.substringBefore("@"), fullName, email)
                    addStudentToDb(student)
                    checkIfUserVerified()
                    sendVerificationEmail()
                    isLoading.value = false
                    isRegisterSuccess.value = it
                }
            }
        } else {
            viewModelScope.launch {
                firebaseRepository.register(email, password) {
                    val teacher = Teacher(fullName, email)
                    addTeacherToDB(teacher)
                    checkIfUserVerified()
                    sendVerificationEmail()
                    isLoading.value = false
                    isRegisterSuccess.value = it
                }
            }
        }
    }

    private fun addStudentToDb(student: Student) {
        viewModelScope.launch {
            firebaseRepository.addStudentToDb(student) {}
        }
    }

    private fun addTeacherToDB(teacher: Teacher) {
        viewModelScope.launch {
            firebaseRepository.addTeacherToDb(teacher) {}
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch {
            firebaseRepository.sendVerification {

            }
        }
    }
}