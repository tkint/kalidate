package com.thomaskint.kalidate.form

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.form.fields.KTypedField
import kotlin.reflect.KProperty1

class KTypedForm<Receiver> private constructor(private val fields: List<KTypedField<Receiver, *>>) {
    fun validate(data: Receiver): List<ValidationError> = fields.map { field -> field.validateReceiver(data) }.flatten()

    class Builder<Receiver> {
        private val fields: MutableList<KTypedField.Builder<Receiver, *>> = mutableListOf()

        fun <DataType : Any> field(property: KProperty1<Receiver, DataType?>): KTypedField.Builder<Receiver, DataType> =
            KTypedField.Builder(property).also { fields.add(it) }

        fun string(property: KProperty1<Receiver, String?>): KTypedField.Builder<Receiver, String> =
            field(property)

        fun boolean(property: KProperty1<Receiver, Boolean?>): KTypedField.Builder<Receiver, Boolean> =
            field(property)

        fun integer(property: KProperty1<Receiver, Int?>): KTypedField.Builder<Receiver, Int> =
            field(property)

        fun double(property: KProperty1<Receiver, Double?>): KTypedField.Builder<Receiver, Double> =
            field(property)

        fun build() = KTypedForm(fields.map { it.build() })
    }
}

fun <T> form(fn: KTypedForm.Builder<T>.() -> Unit) = KTypedForm.Builder<T>().apply(fn)
