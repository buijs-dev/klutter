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
import com.intellij.ui.layout.GrowPolicy
import com.intellij.ui.layout.PropertyBinding
import com.intellij.ui.layout.panel

private val data: KlutterTaskConfig = KlutterTaskConfig()

class KlutterMenu(private val builder: KlutterModuleBuilder) : ModuleWizardStep() {

    override fun getComponent() = panel {
        // Kotlin UI DSL v2: indent
        row {
            row("Name:") {
                cell(isFullWidth = true) {
                    textField(appNameObservable, 1)
                        .withLargeLeftGap()
                        .growPolicy(GrowPolicy.MEDIUM_TEXT)
                }
            }

            row("Group:") {
                cell(isFullWidth = true) {
                    textField(groupNameObservable, 1)
                        .withLargeLeftGap()
                        .growPolicy(GrowPolicy.MEDIUM_TEXT)
                }
            }

            row("Type:") {
                cell(isFullWidth = true) {
                    label("plugin")
                        .withLargeLeftGap()
                        .growPolicy(GrowPolicy.MEDIUM_TEXT)
                }
            }
        }
    }

    override fun updateDataModel() {
        builder.config = data
    }

    override fun validate(): Boolean {
        if(builder.config != null) {
            return builder.config!!.validate()
        }
        return super.validate()
    }

}

private val appNameObservable = PropertyBinding(
    get = { data.appName ?: "my_plugin" },
    set = { value -> data.appName = value }
)

private val groupNameObservable = PropertyBinding(
    get = { data.groupName ?: "com.example" },
    set = { value -> data.groupName = value }
)