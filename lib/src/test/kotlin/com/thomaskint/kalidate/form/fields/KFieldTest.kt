package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class KFieldTest {
    class TestField(
        name: String,
        validators: List<(value: Int?) -> ValidationError?>
    ) : KField<Int>(name, validators) {
        class Builder(name: String) : KField.Builder<Int, TestField>(name) {
            override fun build(): TestField = TestField(name, validators)
        }

        enum class ErrorType : ValidationError.Type {
            TEST_ERROR,
        }
    }

    @Test
    fun `null is valid when no specs`() {
        val spec = TestField.Builder("test-field").build()

        spec.validate(null) shouldBe emptyList()
    }

    @Test
    fun `null is not valid when required`() {
        val spec = TestField.Builder("test-field")
            .required()
            .build()

        spec.validate(null) shouldHaveSingleElement ValidationError(
            type = ValidationError.Type.Builtin.REQUIRED,
            path = "test-field",
        )
    }

    @Test
    fun `custom spec should apply`() {
        val spec = TestField.Builder("test-field")
            .spec(TestField.ErrorType.TEST_ERROR) { it != 12 }
            .build()

        spec.validate(null) shouldHaveSingleElement ValidationError(
            type = TestField.ErrorType.TEST_ERROR,
            path = "test-field",
        )
    }

    @Test
    fun `custom spec should apply with required`() {
        val spec = TestField.Builder("test-field")
            .spec(TestField.ErrorType.TEST_ERROR) { it != 12 }
            .required()
            .build()

        spec.validate(null) shouldContainExactlyInAnyOrder listOf(
            ValidationError(type = ValidationError.Type.Builtin.REQUIRED, path = "test-field"),
            ValidationError(type = TestField.ErrorType.TEST_ERROR, path = "test-field"),
        )
    }

    @Test
    fun `error should build a ValidationError`() {
        val spec = TestField.Builder("test-field").build()

        spec.error(TestField.ErrorType.TEST_ERROR) shouldBe ValidationError(
            type = TestField.ErrorType.TEST_ERROR,
            path = "test-field",
        )
    }
}
