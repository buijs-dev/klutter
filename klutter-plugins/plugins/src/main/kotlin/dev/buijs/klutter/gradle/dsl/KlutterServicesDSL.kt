package dev.buijs.klutter.gradle.dsl

import dev.buijs.klutter.core.DartType
import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.KotlinType


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
 * The KlutterServicesDSL is used by the KlutterGeneratePigeonsTask
 * and returns a KlutterServiceDTO object which is then used to create
 * a Pigeon.dart file.
 *
 */
@DslMarker
internal annotation class KlutterServiceDSLMarker

@KlutterServiceDSLMarker
class KlutterServicesDSL: KlutterDSL<KlutterServiceBuilder> {
    override fun configure(lambda: KlutterServiceBuilder.() -> Unit): KlutterServiceDTO {
        return KlutterServiceBuilder().apply(lambda).build()
    }
}

@KlutterServiceDSLMarker
class KlutterServiceBuilder: KlutterDSLBuilder {
    private val apiList = mutableListOf<KlutterApi>()
    private val dataClassList = mutableListOf<KlutterDataClass>()
    private val enumClassList = mutableListOf<KlutterEnumClass>()

    fun api(name: String, lambda: KlutterApiBuilder.() -> Unit) {
        apiList.add(KlutterApiBuilder().apply(lambda).also { it.name(name) }.build())
    }

    fun dataclass(name: String, lambda: KlutterDataClassBuilder.() -> Unit) {
        dataClassList.add(KlutterDataClassBuilder().apply(lambda).also { it.name(name) }.build())
    }

    fun enumclass(name: String, lambda: KlutterEnumClassBuilder.() -> Unit) {
        enumClassList.add(KlutterEnumClassBuilder().apply(lambda).also { it.name(name) }.build())
    }

    override fun build() = KlutterServiceDTO(
        apis = apiList,
        dataClasses = dataClassList,
        enumClasses = enumClassList
    )
}

@KlutterServiceDSLMarker
class KlutterApiBuilder {

    private var apiName: String? = null
    private val functionList: MutableList<KlutterApiFunction> = mutableListOf()

    fun func(name: String, async: Boolean = false, builder: KlutterApiFunctionBuilder.() -> Unit) {
        functionList.add(
            KlutterApiFunctionBuilder()
            .apply(builder).build()
            .also { it.name = name; it.async = async })
    }

    fun name(name: String){ apiName = name }

    fun build(): KlutterApi = KlutterApi(apiName, functionList)
}

@KlutterServiceDSLMarker
class KlutterApiFunctionBuilder {

    private var functionName: String? = null
    private var returnValue: String? = null
    private var paramsList: List<KlutterMetaNamedField>? = null

    fun takes(builder: KlutterApiFunctionParamsBuilder.() -> Unit) {
        paramsList = KlutterApiFunctionParamsBuilder().apply(builder).build()
    }

    fun gives(builder: KlutterMetaTypeBuilder.() -> Unit) {
        returnValue = KlutterMetaTypeBuilder().apply(builder).build()
    }
    fun build(): KlutterApiFunction = KlutterApiFunction(
        name = functionName,
        returnValue = returnValue,
        parameters = paramsList
    )
}

@KlutterServiceDSLMarker
class KlutterApiFunctionParamsBuilder {
    private val paramsList: MutableList<KlutterMetaNamedField> = mutableListOf()

    fun parameter(builder: KlutterMetaNamedFieldBuilder.() -> Unit) {
        paramsList.add(KlutterMetaNamedFieldBuilder().apply(builder).build())
    }

    fun build(): List<KlutterMetaNamedField> = paramsList

}

@KlutterServiceDSLMarker
class KlutterMetaNamedFieldBuilder {
    var name: String? = null
    var type: String? = null

    fun String(named: String) {
        type = DartType.String.name
        name = named
    }

    fun Int(named: String) {
        type = DartType.int.name
        name = named
    }

    fun Double(named: String) {
        type = DartType.double.name
        name = named
    }

    fun Boolean(named: String) {
        type = DartType.bool.name
        name = named
    }

    fun Defined(field: KlutterMetaNamedField) {
        type = field.type
        name = field.name
    }

    fun DefinedMap(field: KlutterMetaNamedField) {
        type = field.type
        name = field.name
    }

    fun DefinedList(field: KlutterMetaNamedField) {
        type = "List<${field.type}>"
        name = field.name
    }

    fun StringList(named: String) {
        type = "List<${DartType.String.name}>"
        name = named
    }

    fun IntList(named: String) {
        type = "List<${DartType.int.name}>"
        name = named
    }

    fun DoubleList(named: String) {
        type = "List<${DartType.double.name}>"
        name = named
    }

    fun BooleanList(named: String) {
        type = "List<${DartType.bool.name}>"
        name = named
    }

    fun build() = KlutterMetaNamedField(name = name, type = type)
}


@KlutterServiceDSLMarker
class KlutterMetaTypeBuilder {
    private var typeOf: String? = null

    fun nothing() { typeOf = "void" }

    fun String() { typeOf = DartType.String.name }

    fun Int() { typeOf = DartType.int.name }

    fun Double() { typeOf = DartType.double.name }

    fun Boolean() { typeOf = DartType.bool.name }

    fun Defined(type: String) { this.typeOf = type }

    fun DefinedList(type: String) { typeOf = "List<${type}>" }

    fun DefinedMap(type: Pair<String, String>) { typeOf = "Map<${type.first},${type.second}>" }

    fun StringList() { typeOf = "List<${DartType.String.name}>" }

    fun IntList() { typeOf = "List<${DartType.int.name}>" }

    fun DoubleList() { typeOf = "List<${DartType.double.name}>" }

    fun BooleanList() { typeOf = "List<${DartType.bool.name}>" }

    fun build() = typeOf

}


@KlutterServiceDSLMarker
class KlutterDataClassBuilder {

    private var dataClassName: String? = null
    private val fieldsList: MutableList<KlutterMetaNamedField> = mutableListOf()

    fun field(builder: KlutterMetaNamedFieldBuilder.() -> Unit) {
        fieldsList.add(KlutterMetaNamedFieldBuilder().apply(builder).build())
    }

    fun name(name: String){ dataClassName = name }

    fun build(): KlutterDataClass = KlutterDataClass(dataClassName, fieldsList)
}

@KlutterServiceDSLMarker
class KlutterEnumClassBuilder {

    private var enumName: String? = null
    private val valueList: MutableList<String> = mutableListOf()

    fun values(vararg values: String) = valueList.addAll(values)

    fun name(name: String){ enumName = name }

    fun build() = KlutterEnumClass(enumName, valueList)

}

class KotlinMap(private val key: Any, private val value: Any) {

    fun withName(name: String): KlutterMetaNamedField {
        val formattedKey = when (key) {
            is String -> key.toString()

            is KotlinType -> key.name

            else -> throw KlutterCodeGenerationException("Unable to process specified Map.Entry Key. " +
                        "Use either \"String\" or \"KotlinType\"")
        }

        val formattedValue = when (value) {
            is String -> value.toString()

            is KotlinType -> value.name

            else -> throw KlutterCodeGenerationException("Unable to process specified Map.Entry Value. " +
                        "Use either \"String\" or \"KotlinType\"")

        }

        return KlutterMetaNamedField(name = name, type = "Map<$formattedKey,$formattedValue>")
    }




}

infix fun String.named(name: String) = KlutterMetaNamedField(name = name, type = this)

infix fun Pair<String, String>.named(name: String) = KlutterMetaNamedField(name = name, type = "Map<${this.first},${this.second}>")

fun String.asList() = "List<$this>"