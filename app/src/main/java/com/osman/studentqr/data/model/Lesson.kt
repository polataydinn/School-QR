package com.osman.studentqr.data.model

import java.time.DayOfWeek


data class Lesson (
    var listOfStudents: List<Student>? = emptyList(),
    var lessonUUID: String? = "",
    var lessonName: String? = "",
    var teacher: Teacher? = Teacher(),
    var isLessonOnline: Boolean? = false,
    var lessonWeek: String? = "",
    var isLessonActivated: Boolean? = false
)