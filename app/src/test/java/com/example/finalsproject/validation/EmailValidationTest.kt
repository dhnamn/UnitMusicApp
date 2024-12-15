package com.example.finalsproject.validation

import org.junit.Assert
import org.junit.Test

class EmailValidationTest {
    @Test
    fun validEmail1() {
        val email = "validuser01@gmail.com"
        Assert.assertTrue(EmailValidation.isValid(email))
    }

    @Test
    fun validEmail2() {
        val email = "21021234@vnu.edu.vn"
        Assert.assertTrue(EmailValidation.isValid(email))
    }

    @Test
    fun validEmail3() {
        val email = "email-name.company@domain.net"
        Assert.assertTrue(EmailValidation.isValid(email))
    }

    @Test
    fun invalidEmail_noAtSign() {
        val email = "invalid.user.gmail.com"
        Assert.assertFalse(EmailValidation.isValid(email))
    }

    @Test
    fun invalidEmail_emptyName() {
        val email = "@gmail.com"
        Assert.assertFalse(EmailValidation.isValid(email))
    }

    @Test
    fun invalidEmail_noDomain() {
        val email = "trung0503@"
        Assert.assertFalse(EmailValidation.isValid(email))
    }

    @Test
    fun invalidEmail_noTld() {
        val email = "trung0503@gmail"
        Assert.assertFalse(EmailValidation.isValid(email))
    }
}