package com.thomaskint.kalidate.form.fields

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
