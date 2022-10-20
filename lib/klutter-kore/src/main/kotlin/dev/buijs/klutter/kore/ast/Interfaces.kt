/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package dev.buijs.klutter.kore.ast

/**
 * A type that is nullable.
 *
 * Example: String?.
 */
interface Nullable

/**
 * A type that has type parameters.
 *
 * Example: List<Boolean>.
 */
interface Nested

/**
 * Parent for all types in the ast package.
 */
sealed class AbstractType {
    override fun toString(): String =
        this.javaClass.simpleName

    open val className: String =
        javaClass.simpleName
}

/**
 * A user defined class.
 *
 * Example:
 *
 * ```
 * pacakge com.example
 *
 * class Response(
 *    val foo: Foo
 * )
 * ```
 *
 * is CustomType:
 * - name: "Response"
 * - package: "com.example"
 * - fields: []
 *
 */
open class CustomType(
    override val className: String,
    val packageName: String? = null,
    val fields: List<TypeMember> = emptyList()
): AbstractType() {

    open fun copy(fields: List<TypeMember>) =
        CustomType(
            className = className,
            packageName = packageName,
            fields = fields
        )

    override fun toString() = print()

}

/**
 * A nullable variant of [CustomType].
 */
class NullableCustomType(
    className: String,
    packageName: String? = null,
    fields: List<TypeMember> = emptyList()
): CustomType(
    className = className,
    packageName = packageName,
    fields = fields
), Nullable {

    override fun copy(fields: List<TypeMember>) =
        NullableCustomType(
            className = className,
            packageName = packageName,
            fields = fields
        )

    override fun toString() = print()

}

/**
 * A standard Kotlin/Dart type, as defined in [StandardTypeMap].
 */
sealed class StandardType(
    private val type: StandardTypeMap,
): AbstractType() {

    val kotlinType
        get() = type.kotlinType

    val dartType
        get() = type.dartType

    override val className: String
        get() = kotlinType
}

/**
 * Representation of a class field member.
 */
data class TypeMember(
    val name: String,
    val type: AbstractType,
)

private fun CustomType.print() = """
    |CustomType = $className
    |Package = ${packageName ?: "NOT_FOUND"}
    |Fields = ${fields.map { "${it.name}: ${it.type.typeSimplename()}" }}
    |Nullable = ${this.javaClass.interfaces.map { i -> i.simpleName }.contains(object: Nullable {}.javaClass.simpleName)}
""".trimMargin()

private fun AbstractType?.typeSimplename(): String {
    if(this == null) return "#undetermined#"

    val nullable =
        if(this is Nullable) "?" else ""

    return when (this) {

        is CustomType ->
            this.className + nullable

        is MapType -> {
            val type =
                this.kotlinType

            val keyType =
                this.key.typeSimplename()

            val valueType =
                this.value.typeSimplename()


            "$type<$keyType,$valueType>$nullable"
        }

        is ListType -> {
            val type =
                this.kotlinType

            val childType =
                this.child.typeSimplename()

            "$type<$childType>$nullable"
        }

        is StandardType ->
            this.kotlinType + nullable
    }

}