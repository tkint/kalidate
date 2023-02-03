package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type.Builtin
import kotlin.reflect.KProperty1

class KTypedField<Receiver, DataType : Any>(
    private val property: KProperty1<Receiver, DataType?>,
    private val validators: List<(value: DataType?) -> ValidationError?>,
) {
    fun validate(value: DataType?): List<ValidationError> = validators.mapNotNull { it(value) }
    fun validateReceiver(data: Receiver): List<ValidationError> = validate(property.get(data))

    class Builder<Receiver, DataType : Any>(private val property: KProperty1<Receiver, DataType?>) {
        private val validators: MutableList<(value: DataType?) -> ValidationError?> = mutableListOf()

        init {
            if (!property.returnType.isMarkedNullable) spec(Builtin.REQUIRED) { it == null }
        }

        fun spec(fn: (value: DataType?) -> ValidationError?): Builder<Receiver, DataType> =
            apply { validators.add(fn) }

        fun spec(type: ValidationError.Type, fn: (value: DataType?) -> Boolean): Builder<Receiver, DataType> =
            spec { if (fn(it)) error(type) else null }

        private fun error(type: ValidationError.Type): ValidationError = ValidationError(type, property.name)

        fun build(): KTypedField<Receiver, DataType> = KTypedField(property, validators)
    }
}

@JvmName("stringMin")
fun <Receiver> KTypedField.Builder<Receiver, String>.min(min: Int) =
    apply { spec(Builtin.TOO_SMALL) { it?.length < min } }

@JvmName("stringMax")
fun <Receiver> KTypedField.Builder<Receiver, String>.max(max: Int) =
    apply { spec(Builtin.TOO_BIG) { it?.length > max } }

@JvmName("integerMin")
fun <Receiver> KTypedField.Builder<Receiver, Int>.min(min: Int) =
    apply { spec(Builtin.TOO_SMALL) { it < min } }

@JvmName("integerMax")
fun <Receiver> KTypedField.Builder<Receiver, Int>.max(max: Int) =
    apply { spec(Builtin.TOO_BIG) { it > max } }

@JvmName("doubleMin")
fun <Receiver> KTypedField.Builder<Receiver, Double>.min(min: Double) =
    apply { spec(Builtin.TOO_SMALL) { it < min } }

@JvmName("doubleMax")
fun <Receiver> KTypedField.Builder<Receiver, Double>.max(max: Double) =
    apply { spec(Builtin.TOO_BIG) { it > max } }

@JvmName("doubleMinDecimals")
fun <Receiver> KTypedField.Builder<Receiver, Double>.minDecimals(decimals: Int) =
    apply { spec(Builtin.BAD_FORMAT) { it?.decimalPartSize < decimals } }

@JvmName("doubleMaxDecimals")
fun <Receiver> KTypedField.Builder<Receiver, Double>.maxDecimals(decimals: Int) =
    apply { spec(Builtin.BAD_FORMAT) { it?.decimalPartSize > decimals } }
