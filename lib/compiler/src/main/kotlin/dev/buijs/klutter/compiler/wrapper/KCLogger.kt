package dev.buijs.klutter.compiler.wrapper

import com.google.devtools.ksp.processing.KSPLogger
import dev.buijs.klutter.kore.common.maybeCreateFolder
import dev.buijs.klutter.kore.project.klutterBomVersion
import java.io.File

class KCLogger(
    private val kspLogger: KSPLogger,
    outputFolder: File,
) {

    private val logFile = outputFolder
        .maybeCreateFolder(clearIfExists = true)
        .resolve("compiler.log")
        .also { if(it.exists()) it.delete() }
        .also { it.createNewFile() }
        .also { it.appendText("[INFO]: Execution by Compiler version $klutterBomVersion") }

    fun warn(message: String) {
        kspLogger.warn(message)
        writeWarning(message)
    }

    fun info(message: String) {
        kspLogger.info(message)
        writeInfo(message)
    }

    private fun writeWarning(message: String) {
        logFile.appendText("\n[WARN]: $message")
    }

    private fun writeInfo(message: String) {
        logFile.appendText("\n[INFO]: $message")
    }

}