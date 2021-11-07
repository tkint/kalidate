package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError

class KInteger private constructor(
    name: String,
    validators: List<(value: Int?) -> ValidationError?>,
) : KField<Int>(name, validators) {
    class Builder(name: String) : KField.Builder<Int, KInteger>(name) {
        fun min(min: Int) = spec(ValidationError.Type.Builtin.TOO_SMALL) { it < min }
        fun max(max: Int) = spec(ValidationError.Type.Builtin.TOO_BIG) { it > max }

        override fun build(): KInteger = KInteger(name, validators)
    }
}
