package com.thomaskint.kalidate

data class ValidationError(val type: Type, val path: String) {
    interface Type {
        enum class Builtin : Type {
            TOO_SMALL, TOO_BIG, BAD_FORMAT, REQUIRED,
        }
    }
}
