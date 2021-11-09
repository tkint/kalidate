package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import kotlin.reflect.KProperty1

class KTypedField<Receiver, DataType : Any, FieldType : KField<DataType>>(
    name: String,
    validators: List<(value: DataType?) -> ValidationError?>,
    private val property: KProperty1<Receiver, DataType?>,
) : KField<DataType>(name, validators) {
    fun validateReceiver(data: Receiver): List<ValidationError> = validate(property.get(data))

    class Builder<Receiver, DataType : Any>(
        private val property: KProperty1<Receiver, DataType?>,
    ) : KField.Builder<DataType, KTypedField<Receiver, DataType, KField<DataType>>>(property.name) {
        init {
            if (!property.returnType.isMarkedNullable) required()
        }

        override fun build(): KTypedField<Receiver, DataType, KField<DataType>> =
            KTypedField(name, validators, property)
    }
}

@JvmName("stringMin")
fun <Receiver> KTypedField.Builder<Receiver, String>.min(min: Int) =
    apply { spec(ValidationError.Type.Builtin.TOO_SMALL) { it?.length < min } }

@JvmName("stringMax")
fun <Receiver> KTypedField.Builder<Receiver, String>.max(max: Int) =
    apply { spec(ValidationError.Type.Builtin.TOO_BIG) { it?.length > max } }

@JvmName("integerMin")
fun <Receiver> KTypedField.Builder<Receiver, Int>.min(min: Int) =
    apply { spec(ValidationError.Type.Builtin.TOO_SMALL) { it < min } }

@JvmName("integerMax")
fun <Receiver> KTypedField.Builder<Receiver, Int>.max(max: Int) =
    apply { spec(ValidationError.Type.Builtin.TOO_BIG) { it > max } }

@JvmName("doubleMin")
fun <Receiver> KTypedField.Builder<Receiver, Double>.min(min: Double) =
    apply { spec(ValidationError.Type.Builtin.TOO_SMALL) { it < min } }

@JvmName("doubleMax")
fun <Receiver> KTypedField.Builder<Receiver, Double>.max(max: Double) =
    apply { spec(ValidationError.Type.Builtin.TOO_BIG) { it > max } }

@JvmName("doubleMinDecimals")
fun <Receiver> KTypedField.Builder<Receiver, Double>.minDecimals(decimals: Int) =
    apply { spec(KDouble.ErrorType.DECIMAL_PART_TOO_SMALL) { it?.decimalPartSize < decimals } }

@JvmName("doubleMaxDecimals")
fun <Receiver> KTypedField.Builder<Receiver, Double>.maxDecimals(decimals: Int) =
    apply { spec(KDouble.ErrorType.DECIMAL_PART_TOO_BIG) { it?.decimalPartSize > decimals } }
