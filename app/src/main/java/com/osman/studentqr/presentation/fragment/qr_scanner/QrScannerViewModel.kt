package com.osman.studentqr.presentation.fragment.qr_scanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.osman.studentqr.data.Repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    val isSuccess = MutableLiveData<Boolean>()

    fun addStudentToLesson(uuid: String) {
        repository.getStudentAndAddLesson(uuid) {
            isSuccess.value = it
        }
    }
}