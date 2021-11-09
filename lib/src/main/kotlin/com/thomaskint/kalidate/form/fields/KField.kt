package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type.Builtin

abstract class KField<DataType : Any> protected constructor(
    val name: String,
    private val validators: List<(value: DataType?) -> ValidationError?>,
) {
    fun validate(value: DataType?): List<ValidationError> = validators.mapNotNull { it(value) }

    fun error(type: ValidationError.Type) = ValidationError(type, name)

    abstract class Builder<DataType : Any, FieldType : KField<DataType>>(val name: String) {
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
