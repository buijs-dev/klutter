/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package dev.buijs.klutter.jetbrains.shared

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleBuilderListener
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import dev.buijs.klutter.kore.KlutterException
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

/**
 * Builder which returns a wizard to input project details with which a new klutter project is created.
 *
 * This builder is added in the resources/META-INF/plugin.xml as an extension.
 */
class NewProjectBuilder(
    var config: NewProjectConfig? = null
) : ModuleBuilder(), ModuleBuilderListener {

    init { addListener(this) }

    /**
     * Load the [NewProjectWizardStep] where the user can enter the Klutter project configuration.
     */
    override fun getCustomOptionsStep(
        context: WizardContext?,
        parentDisposable: Disposable?,
    ) = tryLoadWizard()
        ?: throw KlutterException("Unsupported Intellij Platform version")

    /**
     * Configure Java SDK.
     */
    @Throws(ConfigurationException::class)
    override fun setupRootModel(rootModel: ModifiableRootModel) {
        rootModel.getModuleExtension(CompilerModuleExtension::class.java).let {
            it.isExcludeOutput = true

            if (myJdk != null) {
                rootModel.sdk = myJdk
            } else {
                rootModel.inheritSdk()
            }

            it.inheritCompilerOutputPath(true)
        }
    }

    /**
     * Generate the actual project.
     */
    override fun moduleCreated(module: Module) {

        val pathToRoot = super.getContentEntryPath()
            ?: throw KlutterException("Unable to determine pathToRoot")

        val config = config
            ?: throw KlutterException("Project configuration is missing")

        // TODO check if flutterVersion is correct/set
        ApplicationManager.getApplication().invokeLater {
            ProgressManager.getInstance().run(
                NewProjectTaskFactory.build(
                    project = module.project,
                    pathToRoot = pathToRoot,
                    config = config))
        }

    }

    override fun getModuleType(): ModuleType<*> = KlutterModuleType()

    override fun getBuilderId(): String = KlutterBundle.bundleId

    override fun getNodeIcon() = KlutterIcons.logo16x16

    override fun getDescription(): String = KlutterBundle.descriptionLong

    override fun getPresentableName() = KlutterBundle.presentableName

    override fun getGroupName() = KlutterBundle.groupName

}

/**
 * Try to create an instance of [NewProjectWizardStep].
 */
private fun NewProjectBuilder.tryLoadWizard() = try {
    log.info("Try loading the NewKlutterProjectWizard")
    NewProjectWizardStep(this)
} catch (e: Throwable) {
    log.info("Failed to load: NewKlutterProjectWizard", e); null
}

/**
 * Klutter module IDE metadata.
 */
private class KlutterModuleType : ModuleType<NewProjectBuilder>(KlutterBundle.moduleId) {

    override fun createModuleBuilder() = NewProjectBuilder()

    override fun getName() = KlutterBundle.groupName

    override fun getDescription() = KlutterBundle.descriptionShort

    override fun getNodeIcon(isOpened: Boolean) = KlutterIcons.logo16x16

}