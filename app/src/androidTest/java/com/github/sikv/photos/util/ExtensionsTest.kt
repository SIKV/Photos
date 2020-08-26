package com.github.sikv.photos.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExtensionsTest {

    @Test
    fun isValidEmail_nullString_returnsFalse() {
        val string: String? = null
        val result = string.isValidEmail()

        assertEquals(false, result)
    }

    @Test
    fun isValidEmail_emptyString_returnsFalse() {
        assertEquals(false, "".isValidEmail())
    }

    @Test
    fun isValidEmail_blankString_returnsFalse() {
        assertEquals(false, "   ".isValidEmail())
    }

    @Test
    fun isValidEmail_validEmail_returnsTrue() {
        assertEquals(true, "anything@example.com".isValidEmail())
        assertEquals(true, "a@example.com".isValidEmail())
        assertEquals(true, "anything@e.com".isValidEmail())
    }

    @Test
    fun isValidEmail_invalidEmail_returnsFalse() {
        assertEquals(false, "anything".isValidEmail())
        assertEquals(false, "anything@".isValidEmail())
        assertEquals(false, "anything@example".isValidEmail())
        assertEquals(false, "@example".isValidEmail())
        assertEquals(false, "example.com".isValidEmail())
    }
}