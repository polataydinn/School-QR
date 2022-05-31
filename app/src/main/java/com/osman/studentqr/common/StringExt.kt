package com.osman.studentqr.common


fun String.toUserName(): String {
    return this.substringBefore("@")
}

fun String.ifStringIsNumber(): Boolean {
    return this.matches("-?\\d+(\\.\\d+)?".toRegex())
}

fun String.isEmailValid(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}