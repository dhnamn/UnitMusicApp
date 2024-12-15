package com.example.finalsproject.validation

object EmailValidation {
    val PATTERN = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$".toRegex()
    fun isValid(s: String) = s.matches(PATTERN)
}