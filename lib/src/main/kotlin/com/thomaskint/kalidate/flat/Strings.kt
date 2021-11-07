package com.thomaskint.kalidate.flat

import com.thomaskint.kalidate.ValidationError.Type
import kotlin.reflect.KProperty0

fun KProperty0<String?>.min(length: Int, type: Type? = null) =
    spec(type ?: Type.Builtin.TOO_SMALL) { it == null || it.length >= length }

fun KProperty0<String?>.max(length: Int, type: Type? = null) =
    spec(type ?: Type.Builtin.TOO_BIG) { it == null || it.length <= length }

fun KProperty0<String?>.regex(test: Regex, type: Type? = null) =
    spec(type ?: Type.Builtin.TOO_SMALL) { it == null || test.matches(it) }

fun KProperty0<String?>.fixed(length: Int, type: Type? = null) = min(length, type).max(length, type)
fun KProperty0<String?>.notBlank(type: Type? = null) = min(1, type)

fun FieldSpec<String?>.min(length: Int, type: Type? = null) = next(field.min(length, type))
fun FieldSpec<String?>.max(length: Int, type: Type? = null) = next(field.max(length, type))
fun FieldSpec<String?>.regex(test: Regex, type: Type? = null) = next(field.regex(test, type))
