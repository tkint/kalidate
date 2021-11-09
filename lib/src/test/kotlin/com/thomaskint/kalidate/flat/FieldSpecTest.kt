package com.thomaskint.kalidate.flat

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class FieldSpecTest {
    data class TestPost(val username: String)

    @Test
    fun `validate returns null when test is ok`() {
        val post = TestPost("jdoe")

        val spec = FieldSpec(
            field = post::username,
            type = Type.Builtin.REQUIRED,
            test = { true },
        )

        spec.validate() shouldBe null
    }

    @Test
    fun `validate returns error when test is ko`() {
        val post = TestPost("jdoe")

        val spec = FieldSpec(
            field = post::username,
            type = Type.Builtin.REQUIRED,
            test = { false },
        )

        spec.validate() shouldBe ValidationError(
            type = Type.Builtin.REQUIRED,
            path = "username",
        )
    }
}
