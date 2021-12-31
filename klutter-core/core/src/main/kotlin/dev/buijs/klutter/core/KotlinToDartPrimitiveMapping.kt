package dev.buijs.klutter.core


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
 * This is not an exhaustive mapping but a basic mapping to facilitate
 * rudimentary type safety in the KotlinServicesDSL.
 *
 */
class KotlinToDartPrimitiveMapping {

    private val mapping: Map<KotlinType, DartType> = mapOf(
        KotlinType.Int to DartType.int,
        KotlinType.Double to DartType.double,
        KotlinType.Boolean to DartType.bool,
        KotlinType.String to DartType.String,
        KotlinType.List to DartType.List,
        KotlinType.Map to DartType.Map)

    fun dart2Kotlin(type: DartType): KotlinType =
        mapping.entries.first { entry -> entry.value == type }.key

    fun kotlin2Dart(type: KotlinType): DartType =
        mapping.entries.first { entry -> entry.key == type }.value

}

enum class KotlinType {
    Int, Double, Boolean, String, List, Map;
}

enum class DartType {
    int, double, String, bool, List, Map
}