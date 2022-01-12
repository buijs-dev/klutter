package dev.buijs.klutter.core


/**
 * @author Gillian Buijs
 *
 * This is not an exhaustive mapping but a basic mapping to facilitate
 * rudimentary type safety in the KotlinServicesDSL.
 *
 */
enum class KotlinDartMap(val kotlinType: String, val dartType: String) {

    KInt("Int", "int"),
    KDouble("Double", "double"),
    KBoolean("Boolean", "bool"),
    KString("String", "String"),
    KList("List", "List"),
    KMap("Map", "Map");

    companion object {

        fun toKotlinType(type: String) = values().firstOrNull { it.kotlinType == type } ?.kotlinType
            ?: throw KlutterCodeGenerationException("No such kotlinType in KotlinDartMap: $type")

        fun toDartType(type: String) = values().firstOrNull { it.dartType == type } ?.dartType
            ?: throw KlutterCodeGenerationException("No such dartType in KotlinDartMap: $type")

    }
}