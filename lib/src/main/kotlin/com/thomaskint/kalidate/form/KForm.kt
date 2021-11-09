package com.thomaskint.kalidate.form

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type.Builtin
import com.thomaskint.kalidate.form.fields.KAny
import com.thomaskint.kalidate.form.fields.KBoolean
import com.thomaskint.kalidate.form.fields.KDouble
import com.thomaskint.kalidate.form.fields.KField
import com.thomaskint.kalidate.form.fields.KInteger
import com.thomaskint.kalidate.form.fields.KString

class KForm private constructor(private val fields: List<KField<*>>) {
    fun validate(data: Map<String, Any?>): List<ValidationError> = fields.map { field ->
        val value = data[field.name]
        when {
            field is KString && value is String? -> field.validate(value)
            field is KBoolean && value is Boolean? -> field.validate(value)
            field is KInteger && value is Int? -> field.validate(value)
            field is KDouble && value is Double? -> field.validate(value)
            field is KAny -> field.validate(value)
            else -> listOf(field.error(Builtin.BAD_FORMAT))
        }
    }.flatten()

    fun validate(vararg data: Pair<String, Any?>) = validate(mapOf(*data))

    class Builder {
        private val fields: MutableList<KField.Builder<*, *>> = mutableListOf()

        fun <DataType, FieldType : KField<DataType>, BuilderType : KField.Builder<DataType, FieldType>> field(builder: BuilderType): BuilderType =
            builder.also { fields.add(it) }

        fun field(name: String): KAny.Builder = KAny.Builder(name).also { fields.add(it) }

        fun string(name: String): KString.Builder = field(KString.Builder(name))
        fun boolean(name: String): KBoolean.Builder = field(KBoolean.Builder(name))
        fun integer(name: String): KInteger.Builder = field(KInteger.Builder(name))
        fun double(name: String): KDouble.Builder = field(KDouble.Builder(name))

        fun build(): KForm = KForm(fields.map { it.build() })
    }
}

fun form(fn: KForm.Builder.() -> Unit) = KForm.Builder().apply(fn)
