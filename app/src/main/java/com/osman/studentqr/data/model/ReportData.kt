package com.osman.studentqr.data.model

data class ReportData(
    val studentName: String? = "",
    val week: List<String> = (0..14).map { " " },
)
