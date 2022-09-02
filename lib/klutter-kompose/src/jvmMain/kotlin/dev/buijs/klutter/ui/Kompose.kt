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
package dev.buijs.klutter.ui

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.KlutterPrinter
import dev.buijs.klutter.kore.shared.KlutterDTO
import dev.buijs.klutter.kore.shared.ClassFileLoader
import dev.buijs.klutter.ui.event.KlutterEvent
import mu.KotlinLogging
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

private const val clazz = "class"

sealed class Kompose<T : KlutterEvent<*, *>>(
    internal val event: KClass<T>,
) : KlutterDTO, KlutterPrinter {

    private val log = KotlinLogging.logger { }

    private val abc = ClassFileLoader.get(event.javaObjectType)
    private val abc3 = abc?.genericSuperclass as? ParameterizedType
    private val abc4 = abc3?.actualTypeArguments


    private val types = ClassFileLoader.get(event.javaObjectType)
        ?.let { it.genericSuperclass as? ParameterizedType }
        ?.actualTypeArguments
        ?:throw KlutterException("Failed to process KlutterEvent impl.")

    val eventType: String = types[0].typeName.removePrefix("$clazz ")
        .also { log.debug { "Kompose is bound to event '$it'" } }

    val controllerType: String = types[1].typeName.removePrefix("$clazz ")
        .also { log.debug { "Kompose is bound to controller '$it'" } }

    internal fun stateType() = types[1]
        .let { ClassFileLoader.get(it) }
        .let { it?.genericSuperclass as? ParameterizedType}
        .let { it?.actualTypeArguments }
        ?.firstOrNull()
        ?.typeName
        ?.removePrefix("$clazz ")
        ?: throw KlutterException("Failed to determine type of state for controller '${controllerType}'")

    abstract fun hasChild(): Boolean

    abstract fun hasChildren(): Boolean

    abstract fun child(): Kompose<*>

    abstract fun children(): List<Kompose<*>>

    internal fun printStateMethod(): String {
        return "_update${stateType().substringAfterLast(".")}State"
    }
}

/**
 * A Kompose class that has one child.
 */
abstract class KomposeWithChild<T: KlutterEvent<*, *>>(
    event: KClass<T>,
    val child: Kompose<*>? = null,
): Kompose<T>(event) {

    override fun hasChild(): Boolean = child != null

    override fun child(): Kompose<*> = child!!

    override fun hasChildren(): Boolean = false

    override fun children(): List<Kompose<*>> = emptyList()

}

/**
 * A Kompose class with more than one child.
 */
abstract class KomposeWithChildren<T: KlutterEvent<*, *>>(
    event: KClass<T>,
    children: List<Kompose<*>>?,
): Kompose<T>(event) {

    internal val children = children ?: emptyList()

    override fun hasChild(): Boolean = false

    override fun hasChildren(): Boolean = children.isNotEmpty()

    override fun child(): Kompose<*> {
        throw KlutterException("Kompose child() not implemented!")
    }

    override fun children(): List<Kompose<*>> = children

    override fun print(): String = """
        |Column(
        |   children: [
        |   ${children.joinToString("\n") { "${it.print()}," }}
        |   ]
        |)
    """.trimMargin()

}

/**
 * A Kompose class that has zero children.
 */
abstract class KomposeWithoutChildren<T : KlutterEvent<*, *>>(
    event: KClass<T>,
) : Kompose<T>(event) {

    override fun hasChild(): Boolean = false

    override fun hasChildren(): Boolean = false

    override fun child(): Kompose<*> {
        throw KlutterException("Kompose child() not implemented!")
    }

    override fun children(): List<Kompose<*>> = emptyList()

}