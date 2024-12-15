package com.example.finalsproject.validation

import org.junit.Assert
import org.junit.Test

class UsernameValidationTest {
    @Test
    fun validName_6characters() {
        val user = "abcxyz"
        Assert.assertTrue(UsernameValidation.isValid(user))
    }

    @Test
    fun validName_20characters() {
        val user = "abcdeABCDEabcdeABCDE"
        Assert.assertTrue(UsernameValidation.isValid(user))
    }

    @Test
    fun validName_underscore() {
        val user = "valid_username"
        Assert.assertTrue(UsernameValidation.isValid(user))
    }

    @Test
    fun validName_space() {
        val user = "name with spaces"
        Assert.assertTrue(UsernameValidation.isValid(user))
    }

    @Test
    fun validName_allLetters1() {
        val user = "abcdefghijklm"
        Assert.assertTrue(UsernameValidation.isValid(user))
    }

    @Test
    fun validName_allLetters() {
        val user = "nopqrstuvwxyz"
        Assert.assertTrue(UsernameValidation.isValid(user))
    }

    @Test
    fun validName_allDigits() {
        val user = "0123456789"
        Assert.assertTrue(UsernameValidation.isValid(user))
    }

    @Test
    fun invalidName_lessThanMinimumLength() {
        val user = "users"
        Assert.assertFalse(UsernameValidation.isValid(user))
    }

    @Test
    fun invalidName_moreThanMaximumLength() {
        val user = "ReallyLongUsername123"
        Assert.assertFalse(UsernameValidation.isValid(user))
    }

    @Test
    fun invalidName_startsWithSpace() {
        val user = " android"
        Assert.assertFalse(UsernameValidation.isValid(user))
    }

    @Test
    fun invalidName_endsWithSpace() {
        val user = "jetpack compose "
        Assert.assertFalse(UsernameValidation.isValid(user))
    }

    @Test
    fun invalidName_consecutiveSpaces() {
        val user = "user  name"
        Assert.assertFalse(UsernameValidation.isValid(user))
    }

    @Test
    fun invalidName_consecutiveUnderscores() {
        val user = "snake__case"
        Assert.assertFalse(UsernameValidation.isValid(user))
    }

    @Test
    fun invalidName_unicodeCharacters() {
        val user = "Nguyễn Quốc Trung"
        Assert.assertFalse(UsernameValidation.isValid(user))
    }
}