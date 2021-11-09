package com.thomaskint.kalidate.form

import com.thomaskint.kalidate.ValidationError
import com.thomaskint.kalidate.ValidationError.Type.Builtin
import com.thomaskint.kalidate.form.fields.KAny
import com.thomaskint.kalidate.form.fields.KBoolean
import com.thomaskint.kalidate.form.fields.KDouble
import com.thomaskint.kalidate.form.fields.KField
import com.thomaskint.kalidate.form.fields.KInteger
import com.thomaskint.kalidate.form.fields.KString
import com.thomaskint.kalidate.form.fields.KTypedField
import com.thomaskint.kalidate.form.fields.max
import com.thomaskint.kalidate.form.fields.maxDecimals
import com.thomaskint.kalidate.form.fields.min
import kotlin.reflect.KProperty1

class KForm private constructor(private val fields: List<KField<*>>) {
    fun validate(data: Map<String, Any>): List<ValidationError> = fields.map { field ->
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

    fun validate(vararg data: Pair<String, Any>) = validate(mapOf(*data))

    class Builder {
        private val fields: MutableList<KField.Builder<*, *>> = mutableListOf()

        fun <DataType, FieldType : KField<DataType>, BuilderType : KField.Builder<DataType, FieldType>> field(builder: BuilderType): BuilderType =
            builder.also { fields.add(it) }

        fun string(name: String): KString.Builder = field(KString.Builder(name))
        fun boolean(name: String): KBoolean.Builder = field(KBoolean.Builder(name))
        fun integer(name: String): KInteger.Builder = field(KInteger.Builder(name))
        fun double(name: String): KDouble.Builder = field(KDouble.Builder(name))

        fun build(): KForm = KForm(fields.map { it.build() })
    }
}

class KTypedForm<Receiver> private constructor(private val fields: List<KTypedField<Receiver, *, *>>) {
    fun validate(data: Receiver): List<ValidationError> = fields.map { field -> field.validateReceiver(data) }.flatten()

    class Builder<Receiver> {
        private val fields: MutableList<KTypedField.Builder<Receiver, *>> = mutableListOf()

        fun <DataType : Any> field(
            property: KProperty1<Receiver, DataType?>,
        ): KTypedField.Builder<Receiver, DataType> =
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

fun form(fn: KForm.Builder.() -> Unit) = KForm.Builder().apply(fn)
fun <T> form(fn: KTypedForm.Builder<T>.() -> Unit) = KTypedForm.Builder<T>().apply(fn)

class CustomField(
    name: String,
) : KAny(name, emptyList()) {
    class Builder(name: String) : KField.Builder<Any, CustomField>(name) {
        override fun build() = CustomField(name)
    }
}

data class Test(
    val firstname: String,
    val lastname: String?,
    val age: Int,
    val height: Double,
)

fun main() {
    val form = form {
        string("firstname").required()
        string("lastname")
        boolean("aille").required()
        double("age").minDecimals(2)
        field(CustomField.Builder("hello"))
    }

    val validator = form.build()

    val errors = validator.validate(
        "firstname" to "boom",
        "age" to 5.0,
        "hello" to "badam",
    )

    // TYPED
    val typedForm = form<Test> {
        string(Test::firstname).min(10).max(20)
        string(Test::lastname).min(10)
        integer(Test::age).min(10)
        double(Test::height).maxDecimals(2)
    }

    val typedValidator = typedForm.build()

    val typedErrors = typedValidator.validate(
        Test(
            firstname = "aaa",
            lastname = null,
            age = 5,
            height = 10.222,
        ),
    )

    println(form)
}
