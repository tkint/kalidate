package com.thomaskint.kalidate.flat

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class CommonKtTest {
    data class TestPost(val username: String)
    enum class TestType : Type { TEST_ERROR }

    @Test
    fun `validate returns empty list when no spec defined`() {
        val post = TestPost("jdoe")

        val result = post.validate {
            listOf()
        }

        result shouldBe emptyList()
    }

    @Test
    fun `validate returns empty list when no error detected`() {
        val post = TestPost("jdoe")

        val result = post.validate {
            listOf(
                ::username.notBlank(),
            )
        }

        result shouldBe emptyList()
    }

    @Test
    fun `validate returns non empty list when an error is detected`() {
        val post = TestPost("")

        val result = post.validate {
            listOf(
                ::username.notBlank(),
            )
        }

        result shouldBe listOf(
            ValidationError(
                type = Type.Builtin.TOO_SMALL,
                path = "username",
            )
        )
    }

    @Test
    fun `validate returns non empty list when an error is detected with custom type`() {
        val post = TestPost("")

        val result = post.validate {
            listOf(
                ::username.notBlank(TestType.TEST_ERROR),
            )
        }

        result shouldBe listOf(
            ValidationError(
                type = TestType.TEST_ERROR,
                path = "username",
            )
        )
    }
}
