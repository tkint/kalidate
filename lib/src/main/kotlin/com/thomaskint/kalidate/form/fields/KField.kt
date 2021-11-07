package com.thomaskint.kalidate.form.fields

import com.thomaskint.kalidate.ValidationError

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
            spec(ValidationError.Type.Builtin.REQUIRED) { it == null }

        fun spec(fn: (value: DataType?) -> ValidationError?): Builder<DataType, FieldType> =
            apply { validators.add(fn) }

        fun spec(type: ValidationError.Type, fn: (value: DataType?) -> Boolean): Builder<DataType, FieldType> =
            spec { if (fn(it)) error(type) else null }

        protected fun error(type: ValidationError.Type): ValidationError =
            ValidationError(type, name)

        abstract fun build(): FieldType
    }
}
