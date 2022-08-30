package dev.buijs.klutter.jetbrains.shared

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.GrowPolicy
import com.intellij.ui.layout.PropertyBinding
import com.intellij.ui.layout.panel
import dev.buijs.klutter.kore.KlutterException
import java.awt.event.ActionEvent
import javax.swing.Action

class NewProjectDialog : DialogWrapper(true) {

    private val data: NewProjectConfigProperties = NewProjectConfigProperties()

    private val isOkEnabled: Boolean = true

    private val finishAction = FinishAction()

    init {
        title = "New Project"
        init()
    }

    override fun createCenterPanel() = panel {
        row {
            row { KlutterBanner }
            row("Name:") {
                cell(isFullWidth = true) {
                    textField(data.appNameObservable, 1)
                        .withLargeLeftGap()
                        .growPolicy(GrowPolicy.MEDIUM_TEXT)
                }
            }

            row("Group:") {
                cell(isFullWidth = true) {
                    textField(data.groupNameObservable, 1)
                        .withLargeLeftGap()
                        .growPolicy(GrowPolicy.MEDIUM_TEXT)
                }
            }

            row("Location:") {
                cell(isFullWidth = true) {
                    textField(data.pathToRootObservable, 1)
                        .withLargeLeftGap()
                        .growPolicy(GrowPolicy.MEDIUM_TEXT)
                }
            }

        }
    }

    override fun createActions(): Array<Action> {
        super.createDefaultActions()
        return arrayOf(finishAction)
    }

    override fun createLeftSideActions(): Array<Action> {
        return arrayOf(cancelAction)
    }

    private inner class FinishAction: DialogWrapperAction("Finish") {

        init { putValue(NAME, "Finish") }

        override fun doAction(e: ActionEvent) {
            if (doValidate() == null) {
                okAction.isEnabled = isOkEnabled
            }

            val validation = data.config.validate()

            if(!validation.isValid) {
                throw ConfigurationException(
                    validation.messages.joinToString("\n") { "- $it" }
                )
            }

            ApplicationManager.getApplication().invokeLater {
                ProgressManager.getInstance().run(
                    NewProjectTaskFactory.build(
                        config = data.config,
                        pathToRoot = data.pathToRoot
                            ?: throw KlutterException("Unable to determine pathToRoot"),
                    )
                )
            }

            doOKAction()
        }

    }

    private inner class NewProjectConfigProperties(
        val config: NewProjectConfig = NewProjectConfig(),
    )  {

        var pathToRoot: String? = null

        val appNameObservable = PropertyBinding(
            get = { config.appName ?: klutterPluginDefaultName },
            set = { value -> config.appName = value }
        )

        val groupNameObservable = PropertyBinding(
            get = { config.groupName ?: klutterPluginDefaultGroup },
            set = { value -> config.groupName = value }
        )

        val pathToRootObservable = PropertyBinding(
            get = { pathToRoot ?: ""},
            set = { value -> pathToRoot = value }
        )
    }

}