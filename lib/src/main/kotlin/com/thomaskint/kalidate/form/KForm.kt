package com.thomaskint.kalidate.form

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.form.fields.KField

class KForm private constructor(private val fields: List<KField<*>>) {
    fun validate(data: Map<String, Any?>): List<ValidationError> = fields.map { field ->
        field.validateAny(data[field.name])
    }.flatten()

    fun validate(vararg data: Pair<String, Any?>) = validate(mapOf(*data))

    class Builder {
        val fields: MutableList<KField.Builder<*>> = mutableListOf()

        inline fun <reified DataType : Any> field(name: String): KField.Builder<DataType> =
            KField.Builder(name, DataType::class).also { fields.add(it) }

        fun string(name: String): KField.Builder<String> = field(name)
        fun boolean(name: String): KField.Builder<Boolean> = field(name)
        fun integer(name: String): KField.Builder<Int> = field(name)
        fun double(name: String): KField.Builder<Double> = field(name)

        fun build(): KForm = KForm(fields.map { it.build() })
    }
}

fun form(fn: KForm.Builder.() -> Unit) = KForm.Builder().apply(fn)
