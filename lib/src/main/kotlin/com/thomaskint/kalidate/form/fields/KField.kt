package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type.Builtin
import kotlin.reflect.KClass

class KField<DataType : Any> private constructor(
    val name: String,
    private val clazz: KClass<DataType>,
    private val validators: List<(value: DataType?) -> ValidationError?>,
) {
    fun validate(value: DataType?): List<ValidationError> = validators.mapNotNull { it(value) }

    fun validateAny(value: Any?): List<ValidationError> =
        if (value == null) validate(null)
        else if (!clazz.isInstance(value)) listOf(error(Builtin.BAD_FORMAT))
        else validate(value as DataType)

    private fun error(type: ValidationError.Type) = ValidationError(type, name)

    class Builder<DataType : Any>(private val name: String, private val clazz: KClass<DataType>) {
        private val validators: MutableList<(value: DataType?) -> ValidationError?> = mutableListOf()

        fun required(): Builder<DataType> =
            spec(Builtin.REQUIRED) { it == null }

        fun spec(fn: (value: DataType?) -> ValidationError?): Builder<DataType> =
            apply { validators.add(fn) }

        fun spec(type: ValidationError.Type, fn: (value: DataType?) -> Boolean): Builder<DataType> =
            spec { if (fn(it)) error(type) else null }

        fun error(type: ValidationError.Type): ValidationError = ValidationError(type, name)

        fun build(): KField<DataType> = KField(name, clazz, validators)
    }
}

@JvmName("stringMin")
fun KField.Builder<String>.min(min: Int): KField.Builder<String> =
    spec(Builtin.TOO_SMALL) { it?.length < min }

@JvmName("stringMax")
fun KField.Builder<String>.max(max: Int): KField.Builder<String> =
    spec(Builtin.TOO_BIG) { it?.length > max }

@JvmName("integerMin")
fun KField.Builder<Int>.min(min: Int): KField.Builder<Int> =
    spec(Builtin.TOO_SMALL) { it < min }

@JvmName("integerMax")
fun KField.Builder<Int>.max(max: Int): KField.Builder<Int> =
    spec(Builtin.TOO_BIG) { it > max }

@JvmName("doubleMin")
fun KField.Builder<Double>.min(min: Double): KField.Builder<Double> =
    spec(Builtin.TOO_SMALL) { it < min }

@JvmName("doubleMax")
fun KField.Builder<Double>.max(max: Double): KField.Builder<Double> =
    spec(Builtin.TOO_BIG) { it > max }

@JvmName("doubleMinDecimals")
fun KField.Builder<Double>.minDecimals(decimals: Int): KField.Builder<Double> =
    spec(Builtin.BAD_FORMAT) { it?.decimalPartSize < decimals }

@JvmName("doubleMaxDecimals")
fun KField.Builder<Double>.maxDecimals(decimals: Int): KField.Builder<Double> =
    spec(Builtin.BAD_FORMAT) { it?.decimalPartSize > decimals }
