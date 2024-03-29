package dev.buijs.klutter.kore.ast

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
 * and contains one or more @Event annotated functions.
 */
class RequestScopedSimpleController(
    override val packageName: String,
    override val className: String,
    override val functions: List<Method>,
): SimpleController(packageName, className), RequestScoped

class SingletonSimpleController(
    override val packageName: String,
    override val className: String,
    override val functions: List<Method>,
): SimpleController(packageName, className), Singleton

/**
 * A Broadcast Controller which is a class with a no-arg constructor
 * which extends KlutterBroadcast.
 */
class SingletonBroadcastController(
    override val packageName: String,
    override val className: String,
    override val functions: List<Method>,
    response: AbstractType,
): BroadcastController(response, packageName, className), Singleton {

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
    response: AbstractType,
): BroadcastController(response, packageName, className), RequestScoped {

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

sealed class BroadcastController(
    val response: AbstractType,
    packageName: String,
    className: String
): Controller(packageName, className)

sealed class SimpleController(
    packageName: String,
    className: String
): Controller(packageName, className)

interface RequestScoped
interface Singleton