package dev.buijs.klutter.jetbrains.systemtest.utils

import com.intellij.remoterobot.RemoteRobot
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class RemoteRobotExtension: AfterTestExecutionCallback, ParameterResolver {

    private val url
        get() = System.getProperty("remote-robot-url")
            ?: "http://127.0.0.1:8082"

    internal val remoteRobot
        get() = RemoteRobot(url)

    override fun supportsParameter(
        parameterContext: ParameterContext?,
        extensionContext: ExtensionContext?,
    ): Boolean = parameterContext
        ?.parameter
        ?.type
        ?.equals(RemoteRobot::class.java)
        ?: false

    override fun resolveParameter(
        parameterContext: ParameterContext?,
        extensionContext: ExtensionContext?,
    ) = remoteRobot

    override fun afterTestExecution(
        context: ExtensionContext?
    ) {
        // nothing
    }

}