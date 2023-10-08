package dev.buijs.klutter.jetbrains.shared

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import dev.buijs.klutter.kore.project.klutterBomVersion
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JPanel

/**
 * Displays a new project screen for Klutter in the standard new project wizard.
 *
 * This wizard can be accessed by going to:
 * - Intellij: File - New - Project... and then in the left pane click on Klutter.
 * - Android Studio: File - New - New Klutter Project... and then in the left pane click on Klutter.
 */
class NewProjectWizardStep(
    private val builder: NewProjectBuilder,
) : ModuleWizardStep() {

    /**
     * State to store user input.
     */
    private val data: NewProjectConfig = NewProjectConfig().also {
        it.appName = klutterPluginDefaultName
        it.groupName = klutterPluginDefaultGroup
    }

    /**
     * Show the panel to receive user input.
     */
    override fun getComponent() = newProjectPanel(data)

    /**
     * Validate the [NewProjectConfig] data and display an error pop-up if invalid.
     */
    override fun validate(): Boolean =
        data.validOrThrows().let { super.validate() }

    /**
     * Update the [NewProjectConfig] data.
     */
    override fun updateDataModel() {
        builder.config = data
    }

}

/**
 * Throws a [ConfigurationException] if the [NewProjectConfig] is not valid.
 *
 * Throwing an exception here enables the UI to display a pop-up showing the messages.
 */
private fun NewProjectConfig.validOrThrows() {
    this.validate().let { validation ->
        if(!validation.isValid)
            validation.messages
                .joinToString("\n") { "- $it" }
                .let { throw ConfigurationException(it) }
    }
}

/**
 * Big shiny banner! \0/
 */
private object KlutterBanner : JPanel() {

    override fun getPreferredSize(): Dimension =
        Dimension(500, 150)

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        IconLoader.getIcon("/klutterBanner.svg", KlutterIcons::class.java).paintIcon(this, g, 0, 25)
    }

}

/**
 * The new project view.
 */
private fun newProjectPanel(data: NewProjectConfig) = panel {
    indent {
        row {
            // KLUTTER BANNER
            cell(KlutterBanner).verticalAlign(VerticalAlign.CENTER)
        }

        group("Project") {
            // Name: [ my_plugin ]
            row("Name: ") { textField()
                .bindText(data.appNameObservable)
                .horizontalAlign(HorizontalAlign.LEFT)
            }

            // Group: [ com.example ]
            row("Group: ") { textField()
                .bindText(data.groupNameObservable)
                .horizontalAlign(HorizontalAlign.LEFT)
            }
        }

        group("Dependencies") {
            // Version: [ 2023.2.1.beta ]
            row("BOM Version: ") { textField()
                .bindText(data.gradleVersionObservable)
                .horizontalAlign(HorizontalAlign.LEFT)
            }
            row { checkBox("Get flutter dependencies from git.")
                .bindSelected(data.useGitForPubDependenciesObservable)
                .horizontalAlign(HorizontalAlign.LEFT)
            }

            row {}.comment("If enabled then all flutter dependencies are pulled from git and not pub.")
        }
    }
}

/**
 * Getter/Setter for the AppName.
 */
private val NewProjectConfig.appNameObservable
    get() = observable(
        get = { this.appName ?: "" },
        set = { this.appName = it  })

/**
 * Getter/Setter for the GroupName (package name).
 */
private val NewProjectConfig.groupNameObservable
    get() = observable(
        get = { this.groupName ?: "" },
        set = { this.groupName = it  })

private val NewProjectConfig.gradleVersionObservable
    get() = observable(
        get = { this.bomVersion ?: klutterBomVersion },
        set = { this.bomVersion = it  })

private val NewProjectConfig.useGitForPubDependenciesObservable
    get() = observable(
        get = { this.useGitForPubDependencies ?: false },
        set = { this.useGitForPubDependencies = it  })

private fun <T> observable(
    get: () -> T,
    set: (T) -> Unit,
) = object : ObservableMutableProperty<T> {

    override fun set(value: T) { set.invoke(value) }

    override fun get() = get.invoke()

    override fun afterChange(listener: (T) -> Unit) {
        // nothing
    }
    override fun afterChange(listener: (T) -> Unit, parentDisposable: Disposable) {
        // nothing
    }

}