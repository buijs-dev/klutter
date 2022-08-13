package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.KlutterException
import java.io.File
import java.util.concurrent.TimeUnit

fun String.execute(runFrom: File): String {
    val process = ProcessBuilder()
        .command(split(" "))
        .directory(runFrom)
        .start()

    process.waitFor(60, TimeUnit.SECONDS)

    if(process.exitValue() != 0) {
        throw KlutterException(
            "Failed to execute command: \n${
                process.errorStream.reader().readText()
            }"
        )
    }

    return process.inputStream.readBytes().decodeToString()
}