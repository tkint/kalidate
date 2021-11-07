package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError

class KString private constructor(
    name: String,
    validators: List<(value: String?) -> ValidationError?>,
) : KField<String>(name, validators) {
    class Builder(name: String) : KField.Builder<String, KString>(name) {
        fun min(length: Int) = spec(ValidationError.Type.Builtin.TOO_SMALL) { it?.length < length }
        fun max(length: Int) = spec(ValidationError.Type.Builtin.TOO_BIG) { it?.length > length }

        override fun build() = KString(name, validators)
    }
}
