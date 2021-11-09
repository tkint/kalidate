package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type.Builtin
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class KFieldTest {
    @Test
    fun `null is valid when no specs`() {
        val spec = KField.Builder("test-field", Int::class).build()

        spec.validate(null) shouldBe emptyList()
    }

    @Test
    fun `null is not valid when required`() {
        val spec = KField.Builder("test-field", Int::class)
            .required()
            .build()

        spec.validate(null) shouldHaveSingleElement ValidationError(
            type = Builtin.REQUIRED,
            path = "test-field",
        )
    }

    @Test
    fun `custom spec should apply`() {
        val spec = KField.Builder("test-field", Int::class)
            .spec(Builtin.BAD_FORMAT) { it != 12 }
            .build()

        spec.validate(null) shouldHaveSingleElement ValidationError(
            type = Builtin.BAD_FORMAT,
            path = "test-field",
        )
    }

    @Test
    fun `custom spec should apply with required`() {
        val spec = KField.Builder("test-field", Int::class)
            .spec(Builtin.BAD_FORMAT) { it != 12 }
            .required()
            .build()

        spec.validate(null) shouldContainExactlyInAnyOrder listOf(
            ValidationError(type = Builtin.REQUIRED, path = "test-field"),
            ValidationError(type = Builtin.BAD_FORMAT, path = "test-field"),
        )
    }

    @Test
    fun `error should build a ValidationError`() {
        val spec = KField.Builder("test-field", Int::class)

        spec.error(Builtin.BAD_FORMAT) shouldBe ValidationError(
            type = Builtin.BAD_FORMAT,
            path = "test-field",
        )
    }
}
