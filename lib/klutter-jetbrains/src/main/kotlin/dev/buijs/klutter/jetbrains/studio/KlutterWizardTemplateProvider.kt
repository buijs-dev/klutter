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
package dev.buijs.klutter.jetbrains.studio

import com.android.tools.idea.wizard.template.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressManager
import dev.buijs.klutter.jetbrains.shared.KlutterImages
import dev.buijs.klutter.jetbrains.shared.KlutterProjectType
import dev.buijs.klutter.jetbrains.shared.KlutterTaskConfig
import dev.buijs.klutter.jetbrains.shared.KlutterTaskFactory

class KlutterWizardTemplateProvider: WizardTemplateProvider() {
    override fun getTemplates() = listOf(klutterTemplate)
}

val klutterTemplate
    get() = template {
        name = "Klutter Plugin"
        description = "Create a new Klutter plugin for Flutter"
        documentationUrl = "https://github.com/buijs-dev/klutter-dart"
        minApi = 21
        category = Category.Other
        formFactor = FormFactor.Mobile
        thumb = { KlutterThumb }

        constraints = listOf(
            TemplateConstraint.Kotlin
        )

        screens = listOf(
            WizardUiContext.ActivityGallery,
            WizardUiContext.FragmentGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule,
        )

//        val defaultAppNameParameter = stringParameter {
//            name = "App name"
//            default = "example_app"
//            help = "Plugin name as specified in the pubspec.yaml"
//            visible = { true }
//            constraints = listOf(Constraint.APP_PACKAGE)
//        }
//
//        widgets = listOf(
//            TextFieldWidget(defaultAppNameParameter),
//            CheckBoxWidget(booleanParameter {
//                name = "yay"
//                default = true
//                visible = { true }
//            })
//        )
//
//        val packageName = stringParameter {
//            name = "xyz"
//            default = klutterPluginDefaultName
//            visible = { true }
//        }
//
//        widgets(
//            PackageNameWidget(packageName),
//        )

        recipe = { data: TemplateData ->
            val config = data as ModuleTemplateData

            val root = config.projectTemplateData.rootDir.also {
                it.deleteRecursively()
                it.mkdir()
            }

            val task = KlutterTaskFactory.build(
                pathToRoot = root.absolutePath,
                config = KlutterTaskConfig(
                    appName = config.name,
                    groupName = config.packageName,
                    projectType = KlutterProjectType.PLUGIN,
                )
            )

            ApplicationManager.getApplication().invokeLater {
                ProgressManager.getInstance().run(task)
            }

        }
    }

private object KlutterThumb: Thumb(
    path = { KlutterImages.templateIcon }
)