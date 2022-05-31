package com.osman.studentqr.data.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.osman.studentqr.common.toUserName
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.data.model.Student
import com.osman.studentqr.data.model.Teacher

class FirebaseRepository {
    suspend fun login(email: String, password: String, onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }

    suspend fun register(email: String, password: String, onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }

    suspend fun sendVerification(onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    suspend fun checkIfVerified(onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.isEmailVerified.let {
            if (it != null && it) {
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    suspend fun addStudentToDb(student: Student, onComplete: (Boolean) -> Unit) {
        student.studentMail?.toUserName()?.let {
            Firebase.database.reference.child("student").child(it).setValue(student)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }
        }
    }

    suspend fun addTeacherToDb(teacher: Teacher, onComplete: (Boolean) -> Unit) {
        teacher.teacherMail?.toUserName()?.let {
            Firebase.database.reference.child("teacher").child(it).setValue(teacher)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }
        }
    }

    suspend fun createNewLesson(lesson: Lesson, completeEvent: (Boolean) -> Unit) {
        Firebase.database.reference.child("lessons").child(lesson.lessonUUID).setValue(lesson)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    completeEvent(true)
                } else {
                    completeEvent(false)
                }
            }
    }

    suspend fun getTeacherName(completeEvent: (String, String) -> Unit) {
        Firebase.database.reference.child("teacher")
            .child(FirebaseAuth.getInstance().currentUser!!.email!!.toUserName()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result!!.getValue(Teacher::class.java)!!.teacherName?.let { teacherName ->
                        completeEvent(
                            teacherName,
                            it.result!!.getValue(Teacher::class.java)!!.teacherMail!!
                        )
                    }
                }
            }


    }
}