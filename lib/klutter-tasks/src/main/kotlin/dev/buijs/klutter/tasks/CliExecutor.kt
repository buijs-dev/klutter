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
    ): String = command.execute(
        runFrom = runFrom,
        timeout = timeout,
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
    ): String {

        val process = ProcessBuilder()
            .command(split(" "))
            .directory(runFrom)
            .start()

        if(timeout == null) {
            process.waitFor()
        } else {
            process.waitFor(timeout, TimeUnit.SECONDS)
        }

        if(process.exitValue() != 0) {
            throw KlutterException(
                "Failed to execute command: \n${
                    process.errorStream.reader().readText()
                }"
            )
        }

        return process.inputStream.readBytes().decodeToString()

    }

}