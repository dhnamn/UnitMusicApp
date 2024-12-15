package com.example.finalsproject.validation

object UsernameValidation {
    const val MIN_LENGTH = 6
    const val MAX_LENGTH = 20
    val CHAR_RANGE = ('A'..'Z')
        .plus('a'..'z')
        .plus('0'..'9')
        .plus('_')
        .plus(' ')

    fun isValid(s: String) =
        (s.length in MIN_LENGTH..MAX_LENGTH) &&
                s.all { it in CHAR_RANGE } &&
                !s.contains("__") && !s.contains("  ") &&
                !s.startsWith(" ") && !s.endsWith(" ")
}