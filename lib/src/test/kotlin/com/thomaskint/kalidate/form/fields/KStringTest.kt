package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class KStringTest {
    @Test
    fun `null is valid when no specs`() {
        val spec = KString.Builder("test-string").build()

        spec.validate(null) shouldBe emptyList()
    }

    @Test
    fun `value is valid when bigger than min`() {
        val spec = KString.Builder("test-string")
            .min(10)
            .build()

        spec.validate("hello world") shouldBe emptyList()
    }

    @Test
    fun `value is not valid when smaller than min`() {
        val spec = KString.Builder("test-string")
            .min(10)
            .build()

        spec.validate("hello") shouldHaveSingleElement ValidationError(
            type = ValidationError.Type.Builtin.TOO_SMALL,
            path = "test-string",
        )
    }

    @Test
    fun `value is valid when smaller than max`() {
        val spec = KString.Builder("test-string")
            .max(10)
            .build()

        spec.validate("hello") shouldBe emptyList()
    }

    @Test
    fun `value is not valid when bigger than max`() {
        val spec = KString.Builder("test-string")
            .max(10)
            .build()

        spec.validate("hello world") shouldHaveSingleElement ValidationError(
            type = ValidationError.Type.Builtin.TOO_BIG,
            path = "test-string",
        )
    }
}
