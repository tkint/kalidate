package com.thomaskint.kalidate.form.fields

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class KBooleanTest {
    @Test
    fun `null is valid when no specs`() {
        val spec = KBoolean.Builder("test-boolean").build()

        spec.validate(null) shouldBe emptyList()
    }
}
