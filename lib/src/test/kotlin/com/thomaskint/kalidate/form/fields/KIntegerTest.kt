package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class KIntegerTest {
    @Test
    fun `null is valid when no specs`() {
        val spec = KInteger.Builder("test-integer").build()

        spec.validate(null) shouldBe emptyList()
    }

    @Test
    fun `value is valid when bigger than min`() {
        val spec = KInteger.Builder("test-integer")
            .min(10)
            .build()

        spec.validate(15) shouldBe emptyList()
    }

    @Test
    fun `value is not valid when smaller than min`() {
        val spec = KInteger.Builder("test-integer")
            .min(10)
            .build()

        spec.validate(5) shouldHaveSingleElement ValidationError(
            type = ValidationError.Type.Builtin.TOO_SMALL,
            path = "test-integer",
        )
    }

    @Test
    fun `value is valid when smaller than max`() {
        val spec = KInteger.Builder("test-integer")
            .max(10)
            .build()

        spec.validate(5) shouldBe emptyList()
    }

    @Test
    fun `value is not valid when bigger than max`() {
        val spec = KInteger.Builder("test-integer")
            .max(10)
            .build()

        spec.validate(15) shouldHaveSingleElement ValidationError(
            type = ValidationError.Type.Builtin.TOO_BIG,
            path = "test-integer",
        )
    }
}
