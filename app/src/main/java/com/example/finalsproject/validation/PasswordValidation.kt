package com.example.finalsproject.validation

object PasswordValidation {
    const val MIN_LENGTH = 8
    const val MAX_LENGTH = 20
    val CODE_RANGE = 32..126

    private val SPECIAL_CHARACTER_CODE_RANGE = (33..47)
        .plus(58..64)
        .plus(91..96)
        .plus(123..126)

    fun isValid(s: String) =
        s.length in MIN_LENGTH .. MAX_LENGTH &&
        (s.all { it.code in CODE_RANGE }) &&
                s.find { it.code in SPECIAL_CHARACTER_CODE_RANGE } != null &&
                s.find { it in '0'..'9' } != null &&
                s.find { it in 'A'..'Z' } != null
}