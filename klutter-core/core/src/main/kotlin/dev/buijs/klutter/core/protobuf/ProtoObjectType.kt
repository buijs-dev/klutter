package dev.buijs.klutter.core.protobuf

/**
 * Type of Proto being either a message or an enum.
 *
 * @author Gillian Buijs
 */
enum class ProtoObjectType(val type: String) {
    MESSAGE("message"),
    ENUM("enum")
}

/**
 * Mapping of Data Type between protobuf and Kotlin.
 *
 * @author Gillian Buijs
 */
enum class ProtoDataType(val type: String, val kotlinType: String) {
    DOUBLE("double", "Double"),
    FLOAT("float", "Float"),
    INTEGER("int32", "Int"),
    LONG("int64", "Long"),
    BOOLEAN("bool", "Boolean"),
    STRING("string", "String"),
    NONE("", "")
}
