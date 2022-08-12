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
package dev.buijs.klutter.jetbrains

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.jetbrains.KlutterIcons.logo


class KlutterModuleBuilder : ModuleBuilder(), ModuleBuilderListener {

    internal var config: KlutterTaskConfig? = null

    init {
        addListener(this)
    }

    override fun getBuilderId(): String = "buijs_software_klutter"

    override fun getNodeIcon() = logo

    override fun getDescription(): String {
        return "Klutter awesomeness let's go!"
    }

    override fun getPresentableName() = "Klutter"

    override fun getGroupName() = "Klutter Framework"

    override fun getCustomOptionsStep(
        context: WizardContext?,
        parentDisposable: Disposable?,
    ): ModuleWizardStep {
        return KlutterMenu(this)
    }

    @Throws(ConfigurationException::class)
    override fun setupRootModel(rootModel: ModifiableRootModel) {
        val compilerModuleExtension = rootModel.getModuleExtension(
            CompilerModuleExtension::class.java
        )
        compilerModuleExtension.isExcludeOutput = true
        if (myJdk != null) {
            rootModel.sdk = myJdk
        } else {
            rootModel.inheritSdk()
        }

        compilerModuleExtension.inheritCompilerOutputPath(true)

    }

    override fun getModuleType(): ModuleType<*> {
        return KlutterModuleType()
    }

    override fun moduleCreated(module: Module) {

        val pathToRoot = super.getContentEntryPath()
            ?: throw KlutterException("No Root!")

        val config = config
            ?: throw KlutterException("No Config!")

        ApplicationManager.getApplication().invokeLater {
            ProgressManager.getInstance().run(
                KlutterTaskFactory.build(
                    project = module.project,
                    pathToRoot = pathToRoot,
                    config = config,
                )
            )
        }

    }
}

class KlutterModuleType : ModuleType<KlutterModuleBuilder>("KLUTTER_MODULE") {

    override fun createModuleBuilder() = KlutterModuleBuilder()

    override fun getName() = "Buijs Software Klutter Framework"

    override fun getDescription() = "Add support for the Klutter Framework"

    override fun getNodeIcon(isOpened: Boolean) = logo

}