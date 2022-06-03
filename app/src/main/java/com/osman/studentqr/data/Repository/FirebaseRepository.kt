package com.osman.studentqr.data.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
        Firebase.database.reference.child("lessons")
            .child(System.currentTimeMillis().toString()).setValue(lesson)
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

    fun getStudentAndAddLesson(uuid: String, completeEvent: (Boolean) -> Unit) {
        Firebase.database.reference.child("student")
            .child(FirebaseAuth.getInstance().currentUser!!.email!!.toUserName()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.getValue(Student::class.java)?.let { student ->
                        addStudentToLesson(uuid, student, completeEvent)
                    }
                }
            }
    }

    suspend fun getTeacherLessons(completeEvent: (List<Lesson>) -> Unit) {
        Firebase.database.reference.child("lessons")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val lessons = mutableListOf<Lesson>()
                    val listOfStudents = mutableListOf<Student>()
                    p0.children.forEach {
                        if (it.child("listOfStudents").value == null) {
                            val item: Lesson? = it.getValue(Lesson::class.java)
                            item?.let { lesson ->
                                if (lesson.teacher?.teacherMail == FirebaseAuth.getInstance().currentUser!!.email!!) {
                                    lessons.add(lesson)
                                }
                            }
                        } else {
                            it.child("listOfStudents").children.forEach {
                                val item: Student? = it.getValue(Student::class.java)
                                item?.let { student ->
                                    listOfStudents.add(student)
                                }
                            }
                            val lessonUUID = it.child("lessonUUID").getValue(String::class.java)
                            val lessonName = it.child("lessonName").getValue(String::class.java)
                            val teacher = it.child("teacher").getValue(Teacher::class.java)
                            val lesson = Lesson(
                                listOfStudents,
                                lessonUUID,
                                lessonName,
                                teacher
                            )
                            lessons.add(lesson)
                        }

                    }
                    completeEvent(lessons.asReversed())
                }
            })
    }

    suspend fun removeLesson(uuid: String, completeEvent: (Boolean) -> Unit) {
        Firebase.database.reference.child("lessons")
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result!!.children.forEach {
                        if (it.getValue(Lesson::class.java)!!.lessonUUID == uuid) {
                            it.ref.removeValue()
                            completeEvent(true)
                        }
                    }
                } else {
                    completeEvent(false)
                }
            }
    }

    suspend fun getListOfStudents(uuid: String, completeEvent: (List<Student>) -> Unit) {
        Firebase.database.reference.child("lessons")
            .addValueEventListener(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val listOfStudents = mutableListOf<Student>()
                    p0.children.forEach {
                        var mLesson = Lesson()
                        if (it.child("listOfStudents").value == null) {
                            val item: Lesson? = it.getValue(Lesson::class.java)
                            item?.let { lesson ->
                                if (lesson.teacher?.teacherMail == FirebaseAuth.getInstance().currentUser!!.email!!) {
                                    mLesson = lesson
                                }
                            }
                        } else {
                            it.child("listOfStudents").children.forEach {
                                val item: Student? = it.getValue(Student::class.java)
                                item?.let { student ->
                                    listOfStudents.add(student)
                                }
                            }
                            val lessonUUID = it.child("lessonUUID").getValue(String::class.java)
                            val lessonName = it.child("lessonName").getValue(String::class.java)
                            val teacher = it.child("teacher").getValue(Teacher::class.java)
                            val lesson = Lesson(
                                listOfStudents,
                                lessonUUID,
                                lessonName,
                                teacher
                            )
                            mLesson = lesson
                        }

                        mLesson.let { lesson ->
                            if (lesson.lessonUUID == uuid) {
                                val students = mutableListOf<Student>()
                                lesson.listOfStudents?.let { listOfStudents ->
                                    students.addAll(
                                        listOfStudents
                                    )
                                }
                                completeEvent(students)
                            }
                        }
                    }
                }
            })
    }

    suspend fun listOfLessonsStudentAttempted(omComplete: (List<Lesson>) -> Unit) {
        Firebase.database.reference.child("lessons")
            .addValueEventListener(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val listOfLessons = mutableListOf<Lesson>()
                    val listOfStudents = mutableListOf<Student>()
                    p0.children.forEach {
                        it.child("listOfStudents").children.forEach { student ->
                            val item: Student? = student.getValue(Student::class.java)
                            if (item?.studentMail == FirebaseAuth.getInstance().currentUser!!.email!!) {
                                val lessonUUID = it.child("lessonUUID").getValue(String::class.java)
                                val lessonName = it.child("lessonName").getValue(String::class.java)
                                val teacher = it.child("teacher").getValue(Teacher::class.java)

                                val lesson = it.child("listOfStudents").children.map {
                                    val item: Student = it.getValue(Student::class.java)!!
                                    item
                                }.toList().let { students ->
                                    Lesson(
                                        listOfStudents = students,
                                        lessonUUID = lessonUUID,
                                        lessonName = lessonName,
                                        teacher = teacher
                                    )
                                }
                                lesson?.let { mLesson -> listOfLessons.add(mLesson) }
                            }
                        }
                    }
                    omComplete(listOfLessons)
                }
            })
    }

    fun addStudentToLesson(uuid: String, student: Student, completeEvent: (Boolean) -> Unit) {
        Firebase.database.reference.child("lessons")
            .get().addOnCompleteListener {
                var isSuccess = false
                if (it.isSuccessful) {
                    it.result!!.children.forEach {
                        if (it.child("lessonUUID").getValue(String::class.java) == uuid) {
                            isSuccess = true
                            student.studentNumber?.let { studentNumber ->
                                it.ref.child("listOfStudents").child(
                                    studentNumber
                                ).setValue(student)
                            }
                            completeEvent(true)
                        }
                    }
                    if (!isSuccess) {
                        completeEvent(false)
                    }
                }

            }
    }
}