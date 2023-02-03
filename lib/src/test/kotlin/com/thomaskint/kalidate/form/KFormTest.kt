package com.thomaskint.kalidate.form

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type.Builtin
import com.thomaskint.kalidate.form.fields.min
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class KFormTest {
    @Test
    fun `validate without fields`() {
        val form = KForm.Builder().build()

        form.validate(mapOf()) shouldBe emptyList()
    }

    @Test
    fun `validate without fields pair style`() {
        val form = KForm.Builder().build()

        form.validate() shouldBe emptyList()
    }

    @Test
    fun `form is valid when field is optional and no value`() {
        val form = KForm.Builder().apply {
            string("test-string")
        }.build()

        form.validate() shouldBe emptyList()
    }

    @Test
    fun `form is valid when field is optional and value is null`() {
        val form = KForm.Builder().apply {
            string("test-string")
        }.build()

        form.validate("test-string" to null) shouldBe emptyList()
    }

    @Test
    fun `form is valid when type of field is string`() {
        val form = KForm.Builder().apply {
            string("test-string")
        }.build()

        form.validate("test-string" to "hello") shouldBe emptyList()
    }

    @Test
    fun `form is not valid when type of field is not string`() {
        val form = KForm.Builder().apply {
            string("test-string")
        }.build()

        form.validate("test-string" to 12) shouldHaveSingleElement ValidationError(
            type = Builtin.BAD_FORMAT,
            path = "test-string",
        )
    }

    @Test
    fun `form is valid when type of field is boolean`() {
        val form = KForm.Builder().apply {
            boolean("test-boolean")
        }.build()

        form.validate("test-boolean" to true) shouldBe emptyList()
    }

    @Test
    fun `form is not valid when type of field is not boolean`() {
        val form = KForm.Builder().apply {
            boolean("test-boolean")
        }.build()

        form.validate("test-boolean" to "hello") shouldHaveSingleElement ValidationError(
            type = Builtin.BAD_FORMAT,
            path = "test-boolean",
        )
    }

    @Test
    fun `form is valid when type of field is integer`() {
        val form = KForm.Builder().apply {
            integer("test-integer")
        }.build()

        form.validate("test-integer" to 12) shouldBe emptyList()
    }

    @Test
    fun `form is not valid when type of field is not integer`() {
        val form = KForm.Builder().apply {
            integer("test-integer")
        }.build()

        form.validate("test-integer" to "hello") shouldHaveSingleElement ValidationError(
            type = Builtin.BAD_FORMAT,
            path = "test-integer",
        )
    }

    @Test
    fun `form is valid when type of field is double`() {
        val form = KForm.Builder().apply {
            double("test-double")
        }.build()

        form.validate("test-double" to 12.0) shouldBe emptyList()
    }

    @Test
    fun `form is not valid when type of field is not double`() {
        val form = KForm.Builder().apply {
            double("test-double")
        }.build()

        form.validate("test-double" to "hello") shouldHaveSingleElement ValidationError(
            type = Builtin.BAD_FORMAT,
            path = "test-double",
        )
    }

    @Test
    fun `form is valid when type of field is any`() {
        val form = KForm.Builder().apply {
            field<Any>("test-any")
        }.build()

        form.validate("test-any" to 12.0) shouldBe emptyList()
    }

    @Test
    fun `form is valid when specs are respected`() {
        val form = KForm.Builder().apply {
            string("test-string").min(10).required()
            boolean("test-boolean")
            integer("test-integer").min(10)
            double("test-double").min(10.0).required()
            field<String>("test-any").spec(Builtin.BAD_FORMAT) { it != "hello" }
        }.build()

        form.validate(
            "test-string" to "hello world",
            "test-boolean" to null,
            "test-integer" to 15,
            "test-double" to 15.0,
            "test-any" to "hello",
        ) shouldBe emptyList()
    }

    @Test
    fun `form is not valid when specs are not respected`() {
        val form = KForm.Builder().apply {
            string("test-string").min(10).required()
            boolean("test-boolean")
            integer("test-integer").min(10)
            double("test-double").min(10.0).required()
            field<String>("test-any").spec(Builtin.BAD_FORMAT) { it != "hello" }
        }.build()

        form.validate(
            "test-string" to "hello",
            "test-boolean" to null,
            "test-integer" to 5,
            "test-double" to null,
            "test-any" to "hello world",
        ) shouldContainExactlyInAnyOrder listOf(
            ValidationError(type = Builtin.TOO_SMALL, path = "test-string"),
            ValidationError(type = Builtin.TOO_SMALL, path = "test-integer"),
            ValidationError(type = Builtin.REQUIRED, path = "test-double"),
            ValidationError(type = Builtin.BAD_FORMAT, path = "test-any"),
        )
    }

    @Test
    fun `form is valid when specs are respected extension style`() {
        val form = form {
            string("test-string").min(10).required()
            boolean("test-boolean")
            integer("test-integer").min(10)
            double("test-double").min(10.0).required()
            field<String>("test-any").spec(Builtin.BAD_FORMAT) { it != "hello" }
        }.build()

        form.validate(
            "test-string" to "hello world",
            "test-boolean" to null,
            "test-integer" to 15,
            "test-double" to 15.0,
            "test-any" to "hello",
        ) shouldBe emptyList()
    }

    @Test
    fun `form is not valid when specs are not respected extension style`() {
        val form = form {
            string("test-string").min(10).required()
            boolean("test-boolean")
            integer("test-integer").min(10)
            double("test-double").min(10.0).required()
            field<String>("test-any").spec(Builtin.BAD_FORMAT) { it != "hello" }
        }.build()

        form.validate(
            "test-string" to "hello",
            "test-boolean" to null,
            "test-integer" to 5,
            "test-double" to null,
            "test-any" to "hello world",
        ) shouldContainExactlyInAnyOrder listOf(
            ValidationError(type = Builtin.TOO_SMALL, path = "test-string"),
            ValidationError(type = Builtin.TOO_SMALL, path = "test-integer"),
            ValidationError(type = Builtin.REQUIRED, path = "test-double"),
            ValidationError(type = Builtin.BAD_FORMAT, path = "test-any"),
        )
    }
}
