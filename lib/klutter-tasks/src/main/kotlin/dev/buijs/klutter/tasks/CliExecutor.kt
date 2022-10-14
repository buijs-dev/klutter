package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterException
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Execute a CLI command.
 *
 */
open class CliExecutor {

    /**
     * Execute a CLI command.
     */
    fun execute(
        /**
         * Folder from where to execute this command.
         */
        runFrom: File,

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
    ): String = command.execute(
        runFrom = runFrom,
        timeout = timeout,
        environment = environment,
    )

    open fun String.execute(
        /**
         * Folder from where to execute this command.
         */
        runFrom: File,

        /**
         * Maximum time in seconds to wait for the command to be executed.
         */
        timeout: Long? = null,

        /**
         * Any environment variable to add.
         */
        environment: Map<String,String> = emptyMap(),
    ): String = ProcessBuilder()
        .command(split(" "))
        .directory(runFrom)
        .also { it.environment().putAll(environment)}
        .start()
        .finish(timeout)

    fun Process.finish(timeout: Long?): String {
        if(timeout == null) {
            waitFor()
        } else {
            waitFor(timeout, TimeUnit.SECONDS)
        }

        if(exitValue() != 0) {
            throw KlutterException(
                "Failed to execute command: \n${
                    errorStream.reader().readText()
                }"
            )
        }

        return inputStream.readBytes().decodeToString()
    }

}