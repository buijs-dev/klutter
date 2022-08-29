package dev.buijs.klutter.jetbrains.intellij

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.options.ConfigurationException
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import dev.buijs.klutter.jetbrains.shared.*

class NewKlutterProjectWizard(
    private val builder: AbstractKlutterModuleBuilder,
) : ModuleWizardStep() {

    private val data: KlutterTaskConfig = KlutterTaskConfig()

    override fun getComponent() = panel {
        indent {
            row {
                cell(KlutterBanner).verticalAlign(VerticalAlign.CENTER)
            }
            row("Name: ") {
                textField()
                    .bindText(appNameObservable)
                    .horizontalAlign(HorizontalAlign.LEFT)
            }
            row("Group: ") {
                textField()
                    .bindText(groupNameObservable)
                    .horizontalAlign(HorizontalAlign.LEFT)
            }
            row {
                comboBox(
                    KlutterProjectType.values()
                    .filter { it == KlutterProjectType.PLUGIN } // application not yet supported
                    .map { it.displayName }.toList()
                )
                    .label("Project:")
                    .bindItem(projectTypeObservable)
                    .horizontalAlign(HorizontalAlign.LEFT)
            }
            row {}.comment("Generate a plugin project. Application project support is coming soon...")
        }
    }

    override fun updateDataModel() {
        builder.config = data
    }

    override fun validate(): Boolean {
        val validation = data.validate()

        if(!validation.isValid) {
            throw ConfigurationException(
                validation.messages.joinToString("\n") { "- $it" }
            )
        }

        return super.validate()
    }

    private val projectTypeObservable = observable(
        get = { data.projectType.displayName },
        set = { value -> data.projectType = KlutterProjectType.from(value) }
    )

    private val appNameObservable = observable(
        get = { data.appName ?: klutterPluginDefaultName },
        set = { value -> data.appName = value }
    )

    private val groupNameObservable = observable(
        get = { data.groupName ?: klutterPluginDefaultGroup },
        set = { value -> data.groupName = value }
    )

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
}