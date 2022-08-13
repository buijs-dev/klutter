package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.KlutterException
import java.io.File
import java.util.concurrent.TimeUnit

fun String.execute(runFrom: File) {
    val process = ProcessBuilder()
        .command(split(" "))
        .directory(runFrom)
        .start()

    val ok = process.waitFor(60, TimeUnit.SECONDS)

    if(!ok || process.exitValue() != 0) {
        throw KlutterException(
            "Failed to execute command: \n${
                process.errorStream.reader().readText()
            }"
        )
    }
}