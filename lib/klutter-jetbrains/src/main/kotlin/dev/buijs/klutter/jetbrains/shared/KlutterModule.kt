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
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import dev.buijs.klutter.jetbrains.shared.KlutterIcons.logo16x16
import dev.buijs.klutter.kore.KlutterException

abstract class AbstractKlutterModuleBuilder : ModuleBuilder(), ModuleBuilderListener {

    var config: KlutterTaskConfig? = null

    override fun getBuilderId(): String = "buijs_software_klutter"

    override fun getNodeIcon() = logo16x16

    override fun getDescription(): String = "" +
            "Klutter is a framework which interconnects Flutter and Kotlin Multiplatform. " +
            "It can be used to create Flutter plugins or standalone apps."

    override fun getPresentableName() = "Klutter"

    override fun getGroupName() = "Klutter Framework"

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