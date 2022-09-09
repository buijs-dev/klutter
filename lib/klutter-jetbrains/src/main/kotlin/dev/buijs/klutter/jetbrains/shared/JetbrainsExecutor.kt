package dev.buijs.klutter.jetbrains.shared

import com.intellij.execution.configurations.GeneralCommandLine
import dev.buijs.klutter.tasks.CliExecutor
import java.io.File

class JetbrainsExecutor: CliExecutor() {

    override fun String.execute(runFrom: File, timeout: Long?): String =
        GeneralCommandLine(this.split(" "))
            .also { it.workDirectory = runFrom }
            .createProcess()
            .finish(timeout)

}