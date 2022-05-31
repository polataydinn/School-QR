package com.osman.studentqr.data.model

data class Lesson (
    val listOfStudents: List<Student> = emptyList(),
    val lessonUUID: String,
    val lessonName: String,
    val teacher: Teacher
)