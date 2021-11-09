package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class KTypedFieldTest {
    data class TestItem(
        val firstname: String,
        val lastname: String?,
        val age: Int,
        val height: Double,
    )

    @Nested
    inner class BuilderTest {
        @Test
        fun `null is not valid when no specs for a non nullable property`() {
            val spec = KTypedField.Builder(TestItem::firstname).build()

            spec.validate(null) shouldHaveSingleElement ValidationError(
                type = ValidationError.Type.Builtin.REQUIRED,
                path = "firstname",
            )
        }

        @Test
        fun `null is valid when no specs for a nullable property`() {
            val spec = KTypedField.Builder(TestItem::lastname)
                .build()

            spec.validate(null) shouldBe emptyList()
        }

        @Test
        fun `null is valid when no specs for a nullable property targeting a receiver`() {
            val spec = KTypedField.Builder(TestItem::lastname)
                .build()

            val item = TestItem(
                firstname = "Jane",
                lastname = null,
                age = 25,
                height = 170.0,
            )

            spec.validateReceiver(item) shouldBe emptyList()
        }
    }

    @Nested
    inner class StringSpecsTest {
        @Test
        fun `value is valid when bigger than min`() {
            val spec = KTypedField.Builder(TestItem::firstname)
                .min(10)
                .build()

            spec.validate("hello world") shouldBe emptyList()
        }

        @Test
        fun `value is not valid when smaller than min`() {
            val spec = KTypedField.Builder(TestItem::firstname)
                .min(10)
                .build()

            spec.validate("hello") shouldHaveSingleElement ValidationError(
                type = ValidationError.Type.Builtin.TOO_SMALL,
                path = "firstname",
            )
        }

        @Test
        fun `value is valid when smaller than max`() {
            val spec = KTypedField.Builder(TestItem::firstname)
                .max(10)
                .build()

            spec.validate("hello") shouldBe emptyList()
        }

        @Test
        fun `value is not valid when bigger than max`() {
            val spec = KTypedField.Builder(TestItem::firstname)
                .max(10)
                .build()

            spec.validate("hello world") shouldHaveSingleElement ValidationError(
                type = ValidationError.Type.Builtin.TOO_BIG,
                path = "firstname",
            )
        }
    }

    @Nested
    inner class IntegerSpecsTest {
        @Test
        fun `value is valid when bigger than min`() {
            val spec = KTypedField.Builder(TestItem::age)
                .min(10)
                .build()

            spec.validate(15) shouldBe emptyList()
        }

        @Test
        fun `value is not valid when smaller than min`() {
            val spec = KTypedField.Builder(TestItem::age)
                .min(10)
                .build()

            spec.validate(5) shouldHaveSingleElement ValidationError(
                type = ValidationError.Type.Builtin.TOO_SMALL,
                path = "age",
            )
        }

        @Test
        fun `value is valid when smaller than max`() {
            val spec = KTypedField.Builder(TestItem::age)
                .max(10)
                .build()

            spec.validate(5) shouldBe emptyList()
        }

        @Test
        fun `value is not valid when bigger than max`() {
            val spec = KTypedField.Builder(TestItem::age)
                .max(10)
                .build()

            spec.validate(15) shouldHaveSingleElement ValidationError(
                type = ValidationError.Type.Builtin.TOO_BIG,
                path = "age",
            )
        }
    }

    @Nested
    inner class DoubleSpecsTest {
        @Test
        fun `value is valid when bigger than min`() {
            val spec = KTypedField.Builder(TestItem::height)
                .min(10.0)
                .build()

            spec.validate(15.0) shouldBe emptyList()
        }

        @Test
        fun `value is not valid when smaller than min`() {
            val spec = KTypedField.Builder(TestItem::height)
                .min(10.0)
                .build()

            spec.validate(5.0) shouldHaveSingleElement ValidationError(
                type = ValidationError.Type.Builtin.TOO_SMALL,
                path = "height",
            )
        }

        @Test
        fun `value is valid when smaller than max`() {
            val spec = KTypedField.Builder(TestItem::height)
                .max(10.0)
                .build()

            spec.validate(5.0) shouldBe emptyList()
        }

        @Test
        fun `value is not valid when bigger than max`() {
            val spec = KTypedField.Builder(TestItem::height)
                .max(10.0)
                .build()

            spec.validate(15.0) shouldHaveSingleElement ValidationError(
                type = ValidationError.Type.Builtin.TOO_BIG,
                path = "height",
            )
        }

        @Test
        fun `value is valid when decimal part is bigger than minDecimals`() {
            val spec = KTypedField.Builder(TestItem::height)
                .minDecimals(2)
                .build()

            spec.validate(10.555) shouldBe emptyList()
        }

        @Test
        fun `value is not valid when decimal part is smaller than minDecimals`() {
            val spec = KTypedField.Builder(TestItem::height)
                .minDecimals(2)
                .build()

            spec.validate(10.5) shouldHaveSingleElement ValidationError(
                type = KDouble.ErrorType.DECIMAL_PART_TOO_SMALL,
                path = "height",
            )
        }

        @Test
        fun `value is valid when decimal part is smaller than maxDecimals`() {
            val spec = KTypedField.Builder(TestItem::height)
                .maxDecimals(2)
                .build()

            spec.validate(10.5) shouldBe emptyList()
        }

        @Test
        fun `value is not valid when decimal part is bigger than maxDecimals`() {
            val spec = KTypedField.Builder(TestItem::height)
                .maxDecimals(2)
                .build()

            spec.validate(10.555) shouldHaveSingleElement ValidationError(
                type = KDouble.ErrorType.DECIMAL_PART_TOO_BIG,
                path = "height",
            )
        }
    }
}
