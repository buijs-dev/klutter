package dev.buijs.klutter.kore.ast

import dev.buijs.klutter.kore.shared.Method

@Deprecated(message = "controller")
data class ControllerData(
    val name: String,
    val dataType: String,
)

/**
 * Data wrapper for classes annotated with @Controller.
 */
sealed class Controller(
   packageName: String,
   className: String,
): CustomType(packageName, className) {

    open val functions: List<Method> = emptyList()

    override fun toString() = this.javaClass.simpleName +
            "(packageName=$packageName, " +
            "className=$className, " +
            "functions=$functions)"

    open fun toDebugString() = this.javaClass.simpleName +
            "\n     packageName=$packageName" +
            "\n     className=$className" +
            "\n     functions=\n${functions.joinToString("\n") { "        $it"}}"
}

/**
 * A Simple Controller which is a class with a no-arg constructor
 * and contains one or more @KlutterAdaptee annotated functions.
 */
class RequestScopedController(
    override val packageName: String,
    override val className: String,
    override val functions: List<Method>,
): Controller(packageName, className), SimpleController, RequestScoped

class SingletonController(
    override val packageName: String,
    override val className: String,
    override val functions: List<Method>,
): Controller(packageName, className), SimpleController, Singleton

/**
 * A Broadcast Controller which is a class with a no-arg constructor
 * which extends KlutterBroadcast.
 */
class SingletonBroadcastController(
    override val packageName: String,
    override val className: String,
    override val functions: List<Method>,
    override val response: AbstractType,
): Controller(packageName, className), BroadcastController, Singleton {

    override fun toString() = this.javaClass.simpleName +
            "(packageName=$packageName, " +
            "className=$className, " +
            "functions=$functions, " +
            "response=$response)"

    override fun toDebugString() = this.javaClass.simpleName +
            "\n     packageName=$packageName" +
            "\n     className=$className" +
            "\n     response=($response)" +
            "\n     functions=\n${functions.joinToString("\n") { "        $it"}}"

}

class RequestScopedBroadcastController(
    override val packageName: String,
    override val className: String,
    override val functions: List<Method>,
    override val response: AbstractType,
): Controller(packageName, className), BroadcastController, RequestScoped {

    override fun toString() = this.javaClass.simpleName +
            "(packageName=$packageName, " +
            "className=$className, " +
            "functions=$functions, " +
            "response=$response)"

    override fun toDebugString() = this.javaClass.simpleName +
            "\n     packageName=$packageName" +
            "\n     className=$className" +
            "\n     response=($response)" +
            "\n     functions=\n${functions.joinToString("\n") { "        $it"}}"

}

interface BroadcastController {
    val response: AbstractType
    val className: String
}
interface SimpleController
interface RequestScoped
interface Singleton