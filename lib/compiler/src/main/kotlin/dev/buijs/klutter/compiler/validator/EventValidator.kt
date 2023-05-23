package dev.buijs.klutter.compiler.validator

import dev.buijs.klutter.kore.ast.*

internal fun AbstractType.isValidRequestParameterType(): Boolean {
    return when(this) {
        is CustomType,
        is EnumType,
        is BooleanType,
        is DoubleType,
        is IntType,
        is LongType,
        is StringType -> true
        is ListType -> {
            when(child) {
                is BooleanType,
                is DoubleType,
                is IntType,
                is LongType -> true
                else -> false
            }
        }
        else -> false
    }
}