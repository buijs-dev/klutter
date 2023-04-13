package dev.buijs.klutter

import dev.buijs.klutter.annotations.KlutterJSON

fun Any?.encode() = when(this) {
    is KlutterJSON<*> -> this.toKJson()
    else -> this
}

fun Any?.decode() = when(this) {
    (this == null) -> null
    is String -> this
    is Int -> this
    is Double -> this
    is Boolean -> this
    is ByteArray -> this
    is IntArray -> this
    is LongArray -> this
    is FloatArray -> this
    is DoubleArray -> this
    is List<*> -> listOrNull(this)
    is Map<*,*> -> mapOrNull(this)
    else -> null
}

fun stringOrNull(data: Any?): String? = when(data) {
    is String -> data
    else -> null
}

fun intOrNull(data: Any?): Int? = when(data) {
    is Int -> data
    else -> null
}

fun doubleOrNull(data: Any?): Double? = when(data) {
    is Double -> data
    else -> null
}

fun booleanOrNull(data: Any?): Boolean? = when(data) {
    is Boolean -> data
    else -> null
}

fun byteArrayOrNull(data: Any?): ByteArray? = when(data) {
    is ByteArray -> data
    else -> null
}

fun intArrayOrNull(data: Any?): IntArray? = when(data) {
    is IntArray -> data
    else -> null
}

fun longArrayOrNull(data: Any?): LongArray? = when(data) {
    is LongArray -> data
    else -> null
}

fun floatArrayOrNull(data: Any?): FloatArray? = when(data) {
    is FloatArray -> data
    else -> null
}

fun doubleArrayOrNull(data: Any?): DoubleArray? = when(data) {
    is DoubleArray -> data
    else -> null
}

fun listOrNull(data: Any?): List<*>? = when(data) {
    is List<*> -> data.map { it.decode() }
    else -> null
}

fun mapOrNull(data: Any?): Map<*,*>? = when(data) {
    is Map<*,*> -> data.map { it.key.decode() to it.value.decode() }.toMap()
    else -> null
}