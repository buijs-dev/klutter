/* Copyright (c) 2021 - 2023 Buijs Software
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
package dev.buijs.klutter.kradle.wizard

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.promptConfirm
import com.github.kinquirer.components.promptInput
import com.github.kinquirer.components.promptList
import dev.buijs.klutter.kore.common.ExcludeFromJacocoGeneratedReport
import dev.buijs.klutter.kradle.command.Open4Test
import dev.buijs.klutter.kradle.shared.build
import dev.buijs.klutter.kradle.shared.clean
import dev.buijs.klutter.kradle.shared.createNewProject
import java.io.File

internal var mrWizard: Wizard = MrWizard()

@Open4Test
@ExcludeFromJacocoGeneratedReport(
    reason = "KInquirer can't be mocked/stubbed")
internal open class MrWizard(private val inquirer: KInquirer = KInquirer): Wizard {
    override fun promptInput(message: String, default: String) =
        inquirer.promptInput(message = message, default = default)

    override fun promptConfirm(message: String, default: Boolean) =
        inquirer.promptConfirm(message = message, default = default)

    override fun promptList(hint: String, message: String, choices: List<String>) =
        inquirer.promptList(hint = hint, message = message, choices = choices)

}

internal interface Wizard {
    fun promptInput(message: String, default: String): String

    fun promptConfirm(message: String, default: Boolean): Boolean

    fun promptList(hint: String, message: String, choices: List<String>): String

    fun runWizard(currentFolder: File) {
        var chosen: WizardAction
        do {
            chosen = askForWizardAction()
            chosen.action(currentFolder)
        } while(chosen != WizardAction.EXIT)
    }

    fun askForWizardAction(): WizardAction {
        val chosen: String = promptList(
            hint = "press Enter to pick",
            message = "What do you want to do?",
            choices = WizardAction.values().map { it.prettyPrinted })
        return WizardAction.values().first { it.prettyPrinted == chosen }
    }
}

internal enum class WizardAction(val prettyPrinted: String, val action: (currentFolder: File) -> Unit) {
    NEW_PROJECT(
        prettyPrinted = "New: Project",
        action = { getNewProjectOptionsByUserInput().confirmNewProjectOptions()?.createNewProject() }),
    GET_FLUTTER_SDK(
        prettyPrinted = "Download: Flutter SDK",
        action = { getFlutterWizard() }),
    CLEAN_CACHE(
        prettyPrinted = "Clean: Klutter Cache",
        action = { mutableListOf("cache").clean() }),
    BUILD_APP(
        prettyPrinted = "Build: App",
        action = { folder -> build(folder) }),
    EXIT(
        prettyPrinted = "Exit",
        action = { })
}