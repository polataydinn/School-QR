package com.osman.studentqr.data.model

data class TeacherLesson(
    val lessonName: String? = "",
    val listOfLessons: List<Lesson>? = emptyList()
)