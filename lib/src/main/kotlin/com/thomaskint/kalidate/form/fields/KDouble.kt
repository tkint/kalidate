package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError

class KDouble private constructor(
    name: String,
    validators: List<(value: Double?) -> ValidationError?>,
) : KField<Double>(name, validators) {
    class Builder(name: String) : KField.Builder<Double, KDouble>(name) {
        fun min(min: Double) = spec(ValidationError.Type.Builtin.TOO_SMALL) { it < min }
        fun max(max: Double) = spec(ValidationError.Type.Builtin.TOO_BIG) { it > max }
        fun minDecimals(decimals: Int) = spec(ErrorType.DECIMAL_PART_TOO_SMALL) { it?.decimalPartSize < decimals }
        fun maxDecimals(decimals: Int) = spec(ErrorType.DECIMAL_PART_TOO_BIG) { it?.decimalPartSize > decimals }

        override fun build() = KDouble(name, validators)
    }

    enum class ErrorType : ValidationError.Type {
        DECIMAL_PART_TOO_SMALL,
        DECIMAL_PART_TOO_BIG,
    }
}
