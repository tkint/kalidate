package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError

open class KAny protected constructor(
    name: String,
    validators: List<(value: Any?) -> ValidationError?>,
) : KField<Any>(name, validators) {
    class Builder(name: String) : KField.Builder<Any, KAny>(name) {
        override fun build() = KAny(name, validators)
    }
}
