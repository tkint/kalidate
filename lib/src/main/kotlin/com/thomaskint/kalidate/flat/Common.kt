package com.thomaskint.kalidate.flat

import com.thomaskint.kalidate.ValidationError
import kotlin.reflect.KProperty0

class FieldSpec<T>(
    val field: KProperty0<T>,
    private val type: ValidationError.Type,
    private val test: ((value: T?) -> Boolean)?,
) {
    private var next: FieldSpec<T>? = null

    fun next(nextSpec: FieldSpec<T>): FieldSpec<T> {
        next = nextSpec
        return this
    }

    fun validate(): ValidationError? = field.get().run {
        if (test?.invoke(this) == false) ValidationError(type, field.name)
        else next?.validate()
    }
}

fun <T : Any> T.validate(specs: T.() -> List<FieldSpec<*>>): List<ValidationError> =
    specs().mapNotNull(FieldSpec<*>::validate)

fun <T> KProperty0<T?>.spec(type: ValidationError.Type, test: (value: T?) -> Boolean) =
    FieldSpec(this, type, test)

fun <T> KProperty0<T?>.required(type: ValidationError.Type? = null) =
    spec(type ?: ValidationError.Type.Builtin.REQUIRED) { it != null }

fun <T> FieldSpec<T?>.required(type: ValidationError.Type? = null) = next(field.required(type))
