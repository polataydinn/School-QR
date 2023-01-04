package com.osman.studentqr.presentation.fragment.lesson_detail

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osman.studentqr.data.Repository.FirebaseRepository
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.data.model.Student
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    val isRemoved = MutableLiveData<Boolean>()
    val studentsList = MutableLiveData<List<Student>>()
    val isEmpty = MutableLiveData<Boolean>()
    var lesson: Lesson = Lesson()
    private var randomUUID: String = ""
    val randomLessonUUID = MutableLiveData("")
    val setTimer = MutableLiveData(0)

    val timer = object: CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            setTimer.value = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)).toInt()
        }

        override fun onFinish() {
            viewModelScope.launch(Dispatchers.IO) {
                changeQrUUID()
            }
        }
    }

    fun startTimer(){
        timer.start()
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


    private suspend fun changeQrUUID() {
        randomUUID = UUID.randomUUID().toString()
        repository.changeQrParameter(lesson, randomUUID){
            randomLessonUUID.value = randomUUID
            startTimer()
        }
    }

    fun setOnlineStatus(lesson: Lesson?, isChecked: Boolean, position: Int) {
        lesson?.let { repository.setIsCheckedStatus(it, isChecked, position) }
    }
}
