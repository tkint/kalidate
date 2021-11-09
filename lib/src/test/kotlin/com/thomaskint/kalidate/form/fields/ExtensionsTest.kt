package com.thomaskint.kalidate.form.fields

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ExtensionsTest {

    @Test
    fun `parts should return integer part`() {
        (10.0).parts shouldBe (10 to 0)
    }

    @Test
    fun `parts should return integer and decimal part`() {
        (10.5).parts shouldBe (10 to 5)
    }

    @Test
    fun `integerPartSize should return 2 when 10`() {
        (10.0).integerPartSize shouldBe 2
    }

    @Test
    fun `integerPartSize should return 1 when ,5`() {
        (.5).integerPartSize shouldBe 1
    }

    @Test
    fun `decimalPartSize should return 1 when 10`() {
        (10.0).decimalPartSize shouldBe 1
    }

    @Nested
    inner class CompareToTest {
        @Test
        fun `compareTo should return false when left is null`() {
            (null > 10) shouldBe false
        }

        @Test
        fun `compareTo should return false when right is null`() {
            (10 > null) shouldBe false
        }

        @Test
        fun `compareTo should return false when both values are null`() {
            (null > null) shouldBe false
        }
    }
}
