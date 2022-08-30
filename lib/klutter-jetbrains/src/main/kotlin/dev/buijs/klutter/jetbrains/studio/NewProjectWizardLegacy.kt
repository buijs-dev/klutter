package dev.buijs.klutter.jetbrains.studio

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.options.ConfigurationException
import dev.buijs.klutter.jetbrains.shared.NewProjectBuilder
import dev.buijs.klutter.jetbrains.shared.NewProjectConfig
import dev.buijs.klutter.jetbrains.shared.validate
import javax.swing.JPanel

class NewProjectWizardLegacy(
    private val builder: NewProjectBuilder,
) : ModuleWizardStep() {

    private val data: NewProjectConfig = NewProjectConfig()

    override fun getComponent() = JPanel()

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

}