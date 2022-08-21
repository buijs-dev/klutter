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

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.options.ConfigurationException
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

class KlutterMenu(private val builder: KlutterModuleBuilder) : ModuleWizardStep() {

    private val data: KlutterTaskConfig = KlutterTaskConfig()

    override fun getComponent() = panel {
        indent {
            row("Name:") {
                textField().bindText(appNameObservable)
            }
            row("Group:") {
                textField().bindText(groupNameObservable)
            }
            row {
                comboBox(KlutterProjectType.values()
                    .filter { it == KlutterProjectType.PLUGIN } // application not yet supported
                    .map { it.displayName }.toList())
                    .label("Project:")
                    .bindItem(projectTypeObservable)
            }.comment("Generate a plugin project. Application project support is coming soon...")
        }
    }

    override fun updateDataModel() {
        builder.config = data
    }

    override fun validate(): Boolean {
        val validConfig = data.validate()

        if(!validConfig) {
            // TODO collect information about whats invalid and display it.
            throw ConfigurationException("Invalid!")
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

