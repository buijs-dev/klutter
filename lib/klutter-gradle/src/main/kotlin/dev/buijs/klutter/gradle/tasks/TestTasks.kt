package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.tasks.AppiumServerStartTask
import dev.buijs.klutter.tasks.AppiumServerStopTask

internal open class AppiumServerStartGradleTask: AbstractTask() {
    override fun klutterTask() = AppiumServerStartTask(
        pathToRootFolder = pathToFlutterApp(),
        pathToTestFolder = pathToTestFolder()
    )
}

internal open class AppiumServerStopGradleTask: AbstractTask() {
    override fun klutterTask() = AppiumServerStopTask(
        project = project()
    )
}