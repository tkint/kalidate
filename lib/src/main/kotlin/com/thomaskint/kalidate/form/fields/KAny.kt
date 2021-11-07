package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError

abstract class KAny(
    name: String,
    validators: List<(value: Any?) -> ValidationError?>,
) : KField<Any>(name, validators)
