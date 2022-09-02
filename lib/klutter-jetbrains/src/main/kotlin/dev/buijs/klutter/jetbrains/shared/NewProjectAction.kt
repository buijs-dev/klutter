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

import com.intellij.icons.AllIcons
import com.intellij.ide.impl.NewProjectUtil
import com.intellij.ide.projectWizard.NewProjectWizard
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.roots.ui.configuration.ModulesProvider.EMPTY_MODULES_PROVIDER
import com.intellij.openapi.wm.impl.welcomeScreen.NewWelcomeScreen

/**
 * Action to start the new project wizard.
 *
 * In this wizard, the [NewProjectWizardStep] can be selected.
 * This is made available in the wizard options through the [NewProjectBuilder].
 * This builder is added in the resources/META-INF/plugin.xml as an extension.
 */
class NewProjectAction : AnAction("New Klutter Project"), DumbAware {

    /**
     * Adds a button named 'New Klutter Project' to the Welcome Screen.
     */
    override fun update(e: AnActionEvent) {
        if (NewWelcomeScreen.isNewWelcomeScreen(e)) {
            e.presentation.icon = AllIcons.Welcome.CreateNewProjectTab
            e.presentation.text = "New Klutter Project"
        }
    }

    /**
     * When the button is clicked then open a new project wizard.
     */
    override fun actionPerformed(e: AnActionEvent) {
        NewProjectUtil.createNewProject(
            NewProjectWizard(null, EMPTY_MODULES_PROVIDER, null)
        )
    }

}