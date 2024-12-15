package com.example.finalsproject.validation

import org.junit.Assert
import org.junit.Test

class PasswordValidationTest {
    @Test
    fun validPassword_8characters() {
        val pw = "_Qwerty1"
        Assert.assertTrue(PasswordValidation.isValid(pw))
    }

    @Test
    fun validPassword_20characters() {
        val pw = "Nguyen Quoc Trung_03"
        Assert.assertTrue(PasswordValidation.isValid(pw))
    }

    @Test
    fun invalidPassword_lessThanMinimumLength() {
        val pw = "_Trung3"
        Assert.assertFalse(PasswordValidation.isValid(pw))
    }

    @Test
    fun invalidPassword_moreThanMaximumLength() {
        val pw = "really_Long_passw0rd_"
        Assert.assertFalse(PasswordValidation.isValid(pw))
    }

    @Test
    fun invalidPassword_noUppercases() {
        val pw = "#password69"
        Assert.assertFalse(PasswordValidation.isValid(pw))
    }

    @Test
    fun invalidPassword_noNumbers() {
        val pw = "Pas\$sword"
        Assert.assertFalse(PasswordValidation.isValid(pw))
    }

    @Test
    fun invalidPassword_noSpecials() {
        val pw = "Pas5word"
        Assert.assertFalse(PasswordValidation.isValid(pw))
    }

    @Test
    fun invalidPassword_unicodeCharacter() {
        val pw = "Mật_khẩu_34"
        Assert.assertFalse(PasswordValidation.isValid(pw))
    }
}