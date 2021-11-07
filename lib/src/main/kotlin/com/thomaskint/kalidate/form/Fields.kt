package com.thomaskint.kalidate.form

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type.Builtin
import com.thomaskint.kalidate.form.KDouble.DoubleErrorType
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty1


abstract class KField<T> protected constructor(
    val name: String,
    private val validators: List<(value: T?) -> ValidationError?>,
) {
    constructor(field: KField<T>) : this(field.name, field.validators)

    fun validate(value: T?): List<ValidationError> = validators.mapNotNull { it(value) }

    fun error(type: ValidationError.Type) = ValidationError(type, name)

    abstract class Builder<DataType, FieldType : KField<DataType>>(val name: String) {
        protected val validators: MutableList<(value: DataType?) -> ValidationError?> = mutableListOf()

        fun required(): Builder<DataType, FieldType> =
            spec(Builtin.REQUIRED) { it == null }

        fun spec(fn: (value: DataType?) -> ValidationError?): Builder<DataType, FieldType> =
            apply { validators.add(fn) }

        fun spec(type: ValidationError.Type, fn: (value: DataType?) -> Boolean): Builder<DataType, FieldType> =
            spec { if (fn(it)) error(type) else null }

        protected fun error(type: ValidationError.Type): ValidationError =
            ValidationError(type, name)

        abstract fun build(): FieldType
    }
}

class KTypedField<Receiver, DataType, FieldType : KField<DataType>>(
    private val field: FieldType,
    private val property: KProperty1<Receiver, DataType?>,
) : KField<DataType>(field) {
    fun validateReceiver(data: Receiver): List<ValidationError> = field.validate(property.get(data))

    class Builder<Receiver, DataType, FieldType : KField<DataType>>(
        private val field: KField.Builder<DataType, FieldType>,
        private val property: KProperty1<Receiver, DataType?>,
    ) : KField.Builder<DataType, KTypedField<Receiver, DataType, FieldType>>(property.name) {
        override fun build(): KTypedField<Receiver, DataType, FieldType> =
            KTypedField(field.apply { validators.forEach { spec(it) } }.build(), property)
    }
}

class KBoolean private constructor(
    name: String,
) : KField<Boolean>(name, emptyList()) {
    class Builder(name: String) : KField.Builder<Boolean, KBoolean>(name) {
        override fun build() = KBoolean(name)
    }
}

class KString private constructor(
    name: String,
    validators: List<(value: String?) -> ValidationError?>,
) : KField<String>(name, validators) {
    class Builder(name: String) : KField.Builder<String, KString>(name) {
        fun min(length: Int) = spec(Builtin.TOO_SMALL) { it?.length < length }
        fun max(length: Int) = spec(Builtin.TOO_BIG) { it?.length > length }

        override fun build() = KString(name, validators)
    }
}

class KInteger private constructor(
    name: String,
    validators: List<(value: Int?) -> ValidationError?>,
) : KField<Int>(name, validators) {
    class Builder(name: String) : KField.Builder<Int, KInteger>(name) {
        fun min(min: Int) = spec(Builtin.TOO_SMALL) { it < min }
        fun max(max: Int) = spec(Builtin.TOO_BIG) { it > max }

        override fun build(): KInteger = KInteger(name, validators)
    }
}

class KDouble private constructor(
    name: String,
    validators: List<(value: Double?) -> ValidationError?>,
) : KField<Double>(name, validators) {
    class Builder(name: String) : KField.Builder<Double, KDouble>(name) {
        fun min(min: Double) = spec(Builtin.TOO_SMALL) { it < min }
        fun max(max: Double) = spec(Builtin.TOO_BIG) { it > max }
        fun minDecimals(decimals: Int) = spec(DoubleErrorType.DECIMAL_PART_TOO_SMALL) { it?.decimalPartSize < decimals }
        fun maxDecimals(decimals: Int) = spec(DoubleErrorType.DECIMAL_PART_TOO_BIG) { it?.decimalPartSize > decimals }

        override fun build() = KDouble(name, validators)
    }

    enum class DoubleErrorType : ValidationError.Type {
        DECIMAL_PART_TOO_SMALL,
        DECIMAL_PART_TOO_BIG,
    }
}

abstract class KAny(
    name: String,
    validators: List<(value: Any?) -> ValidationError?>,
) : KField<Any>(name, validators)

val Double.parts: Pair<Int, Int>
    get() {
        val (integerPart, decimalPart) = toString().split(".")
        return integerPart.toInt() to decimalPart.toInt()
    }

val Double.integerPartSize: Int
    get() = parts.first.toString().length

val Double.decimalPartSize: Int
    get() = parts.second.toString().length

operator fun <T : Number> Comparable<T>?.compareTo(other: T?): Int =
    if (this != null && other != null) compareTo(other) else 0


@JvmName("stringMin")
fun <Receiver> KTypedField.Builder<Receiver, String, KString>.min(min: Int) =
    apply { spec(Builtin.TOO_SMALL) { it?.length < min } }

@JvmName("stringMax")
fun <Receiver> KTypedField.Builder<Receiver, String, KString>.max(max: Int) =
    apply { spec(Builtin.TOO_BIG) { it?.length > max } }


@JvmName("integerMin")
fun <Receiver> KTypedField.Builder<Receiver, Int, KInteger>.min(min: Int) =
    apply { spec(Builtin.TOO_SMALL) { it < min } }

@JvmName("integerMax")
fun <Receiver> KTypedField.Builder<Receiver, Int, KInteger>.max(max: Int) =
    apply { spec(Builtin.TOO_BIG) { it > max } }


@JvmName("doubleMin")
fun <Receiver> KTypedField.Builder<Receiver, Double, KDouble>.min(min: Double) =
    apply { spec(Builtin.TOO_SMALL) { it < min } }

@JvmName("doubleMax")
fun <Receiver> KTypedField.Builder<Receiver, Double, KDouble>.max(max: Double) =
    apply { spec(Builtin.TOO_BIG) { it > max } }

@JvmName("doubleMinDecimals")
fun <Receiver> KTypedField.Builder<Receiver, Double, KDouble>.minDecimals(decimals: Int) =
    apply { spec(DoubleErrorType.DECIMAL_PART_TOO_SMALL) { it?.decimalPartSize < decimals } }

@JvmName("doubleMaxDecimals")
fun <Receiver> KTypedField.Builder<Receiver, Double, KDouble>.maxDecimals(decimals: Int) =
    apply { spec(DoubleErrorType.DECIMAL_PART_TOO_BIG) { it?.decimalPartSize > decimals } }
