package dev.buijs.klutter.tasks

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

var executor: Executor = Executor()

infix fun String.execute(file: File) =
    executor.execute(runFrom = file, command = this)

/**
 * Execute a CLI command.
 *
 */
open class Executor {

    /**
     * Execute a CLI command.
     */
    @JvmOverloads
    open fun execute(
        /**
         * Folder from where to execute this command.
         */
        runFrom: File = File(""),

        /**
         * Maximum time in seconds to wait for the command to be executed.
         */
        timeout: Long? = null,

        /**
         * The command to be executed.
         */
        command: String,

        /**
         * Any environment variable to add.
         */
        environment: Map<String,String> = emptyMap(),
    ): String = ProcessBuilder()
        .command(command.split(" "))
        .directory(runFrom)
        .also { it.environment().putAll(environment)}
        .start()
        .finish(timeout)
}

fun Process.finish(timeout: Long?): String {
    if(timeout == null) waitFor() else waitFor(timeout, TimeUnit.SECONDS)
    if(exitValue() != 0)
        throw IOException("Failed to execute command: \n${errorStream.reader().readText()}")
    return inputStream.readBytes().decodeToString()
}