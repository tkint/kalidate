package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class KDoubleTest {
    @Test
    fun `null is valid when no specs`() {
        val spec = KDouble.Builder("test-double").build()

        spec.validate(null) shouldBe emptyList()
    }

    @Test
    fun `value is valid when bigger than min`() {
        val spec = KDouble.Builder("test-double")
            .min(10.0)
            .build()

        spec.validate(15.0) shouldBe emptyList()
    }

    @Test
    fun `value is not valid when smaller than min`() {
        val spec = KDouble.Builder("test-double")
            .min(10.0)
            .build()

        spec.validate(5.0) shouldHaveSingleElement ValidationError(
            type = ValidationError.Type.Builtin.TOO_SMALL,
            path = "test-double",
        )
    }

    @Test
    fun `value is valid when smaller than max`() {
        val spec = KDouble.Builder("test-double")
            .max(10.0)
            .build()

        spec.validate(5.0) shouldBe emptyList()
    }

    @Test
    fun `value is not valid when bigger than max`() {
        val spec = KDouble.Builder("test-double")
            .max(10.0)
            .build()

        spec.validate(15.0) shouldHaveSingleElement ValidationError(
            type = ValidationError.Type.Builtin.TOO_BIG,
            path = "test-double",
        )
    }

    @Test
    fun `value is valid when decimal part is bigger than minDecimals`() {
        val spec = KDouble.Builder("test-double")
            .minDecimals(2)
            .build()

        spec.validate(10.555) shouldBe emptyList()
    }

    @Test
    fun `value is not valid when decimal part is smaller than minDecimals`() {
        val spec = KDouble.Builder("test-double")
            .minDecimals(2)
            .build()

        spec.validate(10.5) shouldHaveSingleElement ValidationError(
            type = KDouble.ErrorType.DECIMAL_PART_TOO_SMALL,
            path = "test-double",
        )
    }

    @Test
    fun `value is valid when decimal part is smaller than maxDecimals`() {
        val spec = KDouble.Builder("test-double")
            .maxDecimals(2)
            .build()

        spec.validate(10.5) shouldBe emptyList()
    }

    @Test
    fun `value is not valid when decimal part is bigger than maxDecimals`() {
        val spec = KDouble.Builder("test-double")
            .maxDecimals(2)
            .build()

        spec.validate(10.555) shouldHaveSingleElement ValidationError(
            type = KDouble.ErrorType.DECIMAL_PART_TOO_BIG,
            path = "test-double",
        )
    }
}
