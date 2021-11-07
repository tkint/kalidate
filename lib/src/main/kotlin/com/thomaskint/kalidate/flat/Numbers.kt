package com.thomaskint.kalidate.flat

import com.thomaskint.kalidate.ValidationError.Type
import kotlin.reflect.KProperty0

fun <T : Number> KProperty0<T?>.min(length: T, type: Type? = null) =
    spec(type ?: Type.Builtin.TOO_SMALL) { it == null || it.toDouble() >= length.toDouble() }

fun <T : Number> KProperty0<T?>.max(length: T, type: Type? = null) =
    spec(type ?: Type.Builtin.TOO_BIG) { it == null || it.toDouble() <= length.toDouble() }

fun <T : Number> FieldSpec<T?>.min(length: T, type: Type? = null) = next(field.min(length, type))
fun <T : Number> FieldSpec<T?>.max(length: T, type: Type? = null) = next(field.max(length, type))
