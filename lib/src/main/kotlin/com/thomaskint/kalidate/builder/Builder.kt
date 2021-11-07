package com.thomaskint.kalidate.builder

import com.thomaskint.kalidate.ValidationError
import kotlin.reflect.KProperty1

class FieldSpec<T : Any, U : Any>(
    internal val field: KProperty1<T, U?>,
    private val type: ValidationError.Type,
    private val test: (value: U?) -> Boolean,
    internal val path: String = field.name,
) {
    private var next: FieldSpec<T, U>? = null

    internal fun next(nextSpec: FieldSpec<T, U>): FieldSpec<T, U> {
        next = nextSpec
        return this
    }

    fun validate(receiver: T): ValidationError? = field.get(receiver).run {
        if (!test(this)) ValidationError(type, path)
        else next?.validate(receiver)
    }
}

class ValidationBuilder<T : Any> internal constructor(private val path: String? = null) {
    internal val specs = mutableListOf<FieldSpec<T, *>>()
    internal val children: MutableMap<KProperty1<T, *>, Validation<Any>> = mutableMapOf()

    private fun <U : Any> buildPath(field: KProperty1<T, U?>) = "${path?.let { "$path." } ?: ""}${field.name}"

    fun <U : Any> nested(field: KProperty1<T, U?>, fn: ValidationBuilder<U>.() -> Unit) {
        children[field] = validator(buildPath(field), fn) as Validation<Any>
    }

    fun <U : Any> KProperty1<T, U?>.spec(type: ValidationError.Type, test: (value: U?) -> Boolean) =
        FieldSpec(this, type, test, buildPath(this)).also { specs.add(it) }

    fun <U : Any> FieldSpec<T, U>.spec(type: ValidationError.Type, test: ((value: U?) -> Boolean)) =
        next(FieldSpec(field, type, test, path))

    // Common

    fun <U : Any> KProperty1<T, U?>.required(type: ValidationError.Type? = null) =
        spec(type ?: ValidationError.Type.Builtin.TOO_SMALL) { it != null }

    // Strings

    @JvmName("stringMin")
    fun KProperty1<T, String?>.min(length: Int, type: ValidationError.Type? = null) =
        spec(type ?: ValidationError.Type.Builtin.TOO_SMALL) { it == null || it.length >= length }

    @JvmName("stringMin")
    fun FieldSpec<T, String>.min(length: Int, type: ValidationError.Type? = null) = next(field.min(length, type))

    @JvmName("stringMax")
    fun KProperty1<T, String?>.max(length: Int, type: ValidationError.Type? = null) =
        spec(type ?: ValidationError.Type.Builtin.TOO_BIG) { it == null || it.length <= length }

    @JvmName("stringMax")
    fun FieldSpec<T, String>.max(length: Int, type: ValidationError.Type? = null) = next(field.max(length, type))


    // Numbers
    @JvmName("numberMin")
    fun <U : Number> KProperty1<T, U?>.min(limit: U, type: ValidationError.Type? = null) =
        spec(type ?: ValidationError.Type.Builtin.TOO_SMALL) { it == null || it.toDouble() >= limit.toDouble() }

    @JvmName("numberMin")
    fun <U : Number> FieldSpec<T, U>.min(limit: U, type: ValidationError.Type? = null) = next(field.min(limit, type))

    @JvmName("numberMax")
    fun <U : Number> KProperty1<T, U?>.max(limit: U, type: ValidationError.Type? = null) =
        spec(type ?: ValidationError.Type.Builtin.TOO_BIG) { it == null || it.toDouble() <= limit.toDouble() }

    @JvmName("numberMax")
    fun <U : Number> FieldSpec<T, U>.max(limit: U, type: ValidationError.Type? = null) = next(field.max(limit, type))

}

class Validation<T : Any> internal constructor(
    private val specs: List<FieldSpec<T, *>>,
    private val children: Map<KProperty1<T, *>, Validation<Any>>,
) {
    fun validate(item: T): List<ValidationError> = listOfNotNull(
        specs.mapNotNull { it.validate(item) },
        children.mapNotNull { (field, child) -> field.get(item)?.let(child::validate) }.flatten()
    ).flatten()
}

fun <T : Any> validator(path: String? = null, fn: ValidationBuilder<T>.() -> Unit) =
    ValidationBuilder<T>(path).apply(fn).run { Validation(specs.toList(), children) }
