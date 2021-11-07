package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import kotlin.reflect.KProperty1

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

@JvmName("stringMin")
fun <Receiver> KTypedField.Builder<Receiver, String, KString>.min(min: Int) =
    apply { spec(ValidationError.Type.Builtin.TOO_SMALL) { it?.length < min } }

@JvmName("stringMax")
fun <Receiver> KTypedField.Builder<Receiver, String, KString>.max(max: Int) =
    apply { spec(ValidationError.Type.Builtin.TOO_BIG) { it?.length > max } }


@JvmName("integerMin")
fun <Receiver> KTypedField.Builder<Receiver, Int, KInteger>.min(min: Int) =
    apply { spec(ValidationError.Type.Builtin.TOO_SMALL) { it < min } }

@JvmName("integerMax")
fun <Receiver> KTypedField.Builder<Receiver, Int, KInteger>.max(max: Int) =
    apply { spec(ValidationError.Type.Builtin.TOO_BIG) { it > max } }


@JvmName("doubleMin")
fun <Receiver> KTypedField.Builder<Receiver, Double, KDouble>.min(min: Double) =
    apply { spec(ValidationError.Type.Builtin.TOO_SMALL) { it < min } }

@JvmName("doubleMax")
fun <Receiver> KTypedField.Builder<Receiver, Double, KDouble>.max(max: Double) =
    apply { spec(ValidationError.Type.Builtin.TOO_BIG) { it > max } }

@JvmName("doubleMinDecimals")
fun <Receiver> KTypedField.Builder<Receiver, Double, KDouble>.minDecimals(decimals: Int) =
    apply { spec(KDouble.DoubleErrorType.DECIMAL_PART_TOO_SMALL) { it?.decimalPartSize < decimals } }

@JvmName("doubleMaxDecimals")
fun <Receiver> KTypedField.Builder<Receiver, Double, KDouble>.maxDecimals(decimals: Int) =
    apply { spec(KDouble.DoubleErrorType.DECIMAL_PART_TOO_BIG) { it?.decimalPartSize > decimals } }
