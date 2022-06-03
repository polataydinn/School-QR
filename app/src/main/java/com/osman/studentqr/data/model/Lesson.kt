package com.osman.studentqr.data.model


data class Lesson (
    var listOfStudents: List<Student>? = emptyList(),
    var lessonUUID: String? = "",
    var lessonName: String? = "",
    var teacher: Teacher? = Teacher(),
)