package com.thomaskint.kalidate.form

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type.Builtin
import com.thomaskint.kalidate.form.fields.min
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class KTypedFormTest {
    private data class TestItem(
        val firstname: String,
        val lastname: String?,
        val alive: Boolean,
        val age: Int,
        val height: Double,
    )

    @Test
    fun `validate without fields`() {
        val form = KTypedForm.Builder<TestItem>().build()

        val item = TestItem(
            firstname = "Jane",
            lastname = null,
            alive = true,
            age = 25,
            height = 170.0,
        )

        form.validate(item) shouldBe emptyList()
    }

    @Test
    fun `validate without fields extension style`() {
        val form = form<TestItem> { }.build()

        val item = TestItem(
            firstname = "Jane",
            lastname = null,
            alive = true,
            age = 25,
            height = 170.0,
        )

        form.validate(item) shouldBe emptyList()
    }

    @Test
    fun `form is valid when specs are respected`() {
        val form = KTypedForm.Builder<TestItem>().apply {
            string(TestItem::firstname).min(1)
            string(TestItem::lastname).min(5)
            boolean(TestItem::alive)
            integer(TestItem::age).min(20)
            double(TestItem::height).min(150.0)
        }.build()

        val item = TestItem(
            firstname = "Jane",
            lastname = null,
            alive = true,
            age = 25,
            height = 170.0,
        )

        form.validate(item) shouldBe emptyList()
    }

    @Test
    fun `form is not valid when specs are not respected`() {
        val form = KTypedForm.Builder<TestItem>().apply {
            string(TestItem::firstname).min(1)
            string(TestItem::lastname).min(5)
            boolean(TestItem::alive)
            integer(TestItem::age).min(20)
            double(TestItem::height).min(150.0)
        }.build()

        val item = TestItem(
            firstname = "",
            lastname = "Doe",
            alive = true,
            age = 13,
            height = 140.0,
        )

        form.validate(item) shouldContainExactlyInAnyOrder listOf(
            ValidationError(type = Builtin.TOO_SMALL, path = "firstname"),
            ValidationError(type = Builtin.TOO_SMALL, path = "lastname"),
            ValidationError(type = Builtin.TOO_SMALL, path = "age"),
            ValidationError(type = Builtin.TOO_SMALL, path = "height"),
        )
    }

    @Test
    fun `form is valid when specs are respected extension style`() {
        val form = form<TestItem> {
            string(TestItem::firstname).min(1)
            string(TestItem::lastname).min(5)
            boolean(TestItem::alive)
            integer(TestItem::age).min(20)
            double(TestItem::height).min(150.0)
        }.build()

        val item = TestItem(
            firstname = "Jane",
            lastname = null,
            alive = true,
            age = 25,
            height = 170.0,
        )

        form.validate(item) shouldBe emptyList()
    }

    @Test
    fun `form is not valid when specs are not respected extension style`() {
        val form = form<TestItem> {
            string(TestItem::firstname).min(1)
            string(TestItem::lastname).min(5)
            boolean(TestItem::alive)
            integer(TestItem::age).min(20)
            double(TestItem::height).min(150.0)
        }.build()

        val item = TestItem(
            firstname = "",
            lastname = "Doe",
            alive = true,
            age = 13,
            height = 140.0,
        )

        form.validate(item) shouldContainExactlyInAnyOrder listOf(
            ValidationError(type = Builtin.TOO_SMALL, path = "firstname"),
            ValidationError(type = Builtin.TOO_SMALL, path = "lastname"),
            ValidationError(type = Builtin.TOO_SMALL, path = "age"),
            ValidationError(type = Builtin.TOO_SMALL, path = "height"),
        )
    }
}
