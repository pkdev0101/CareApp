package com.careapp.app.util

fun formatPhoneNumber(phone: String): String {
    val digits = phone.filter(Char::isDigit).take(10)
    return if (digits.length == 10) {
        "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
    } else phone
}
