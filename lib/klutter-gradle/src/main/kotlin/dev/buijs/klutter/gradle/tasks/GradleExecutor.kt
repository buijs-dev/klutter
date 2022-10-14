package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.tasks.CliExecutor
import org.gradle.api.Project
import java.io.File

class GradleExecutor(private val project: Project): CliExecutor() {
    override fun String.execute(
        runFrom: File,
        timeout: Long?,
        environment: Map<String, String>,
    ): String {
        project.exec {
            environment.forEach { (k, v) -> it.environment(k,v) }
            it.workingDir(runFrom)
            it.commandLine(this.split(" "))
        }
        return ""
    }
}