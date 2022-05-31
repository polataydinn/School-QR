package com.osman.studentqr.presentation.fragment.authentication.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.data.Repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>(false)
    val isLoginSuccess = MutableLiveData<Boolean>()
    val isUserVerified = MutableLiveData<Boolean>(false)

    init {
        checkIfUserVerified()
    }

    fun checkIfUserVerified() {
        viewModelScope.launch {
            firebaseRepository.checkIfVerified {
                isUserVerified.value = it
            }
        }
    }

    fun login(email: String, password: String) {
        isLoading.value = true
        viewModelScope.launch {
            firebaseRepository.login(email, password) {
                checkIfUserVerified()
                isLoginSuccess.value = it
                isLoading.value = false
            }
        }
    }
}