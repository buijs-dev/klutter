package dev.buijs.klutter.plugins.gradle.utils

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

internal fun String.runCommand(workingDir: File): String? {

    val process = Runtime.getRuntime()
        .exec(arrayOf("sh", "-c", this), null, workingDir)

    BufferedReader(InputStreamReader(process.inputStream)).let { input ->
        var line = input.readLine()

        while (line != null) {
            println(line)
            line = input.readLine()
        }

        input.close()
    }

    BufferedReader(InputStreamReader(process.errorStream)).let { input ->
        var line = input.readLine()

        while (line != null) {
            println(line)
            line = input.readLine()
        }

        input.close()
    }

    val code = process.waitFor()

    return if(code == 0) null else code.toString()

}