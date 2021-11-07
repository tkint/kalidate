package com.thomaskint.kalidate.form.fields

class KBoolean private constructor(
    name: String,
) : KField<Boolean>(name, emptyList()) {
    class Builder(name: String) : KField.Builder<Boolean, KBoolean>(name) {
        override fun build() = KBoolean(name)
    }
}
