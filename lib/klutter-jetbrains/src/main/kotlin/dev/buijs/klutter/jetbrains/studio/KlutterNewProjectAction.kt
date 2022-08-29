package dev.buijs.klutter.jetbrains.studio

import com.intellij.icons.AllIcons
import com.intellij.ide.impl.NewProjectUtil
import com.intellij.ide.projectWizard.NewProjectWizard
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.wm.impl.welcomeScreen.NewWelcomeScreen
import com.intellij.ui.LayeredIcon
import javax.swing.Icon

class KlutterNewProjectAction : AnAction("New Klutter Project"), DumbAware {
    override fun update(e: AnActionEvent) {
        if (NewWelcomeScreen.isNewWelcomeScreen(e)) {
            e.presentation.icon = klutterDecoratedIcon
            e.presentation.text = "New Klutter Project"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val wizard = NewProjectWizard(null, ModulesProvider.EMPTY_MODULES_PROVIDER, null)
        NewProjectUtil.createNewProject(wizard)
    }

    private val klutterDecoratedIcon: Icon
        get() {
            val icon = AllIcons.Welcome.CreateNewProjectTab
            //val badgeIcon: Icon = OffsetIcon(0, KlutterIcons.buttonIcon).scale(0.1F)
            val decorated = LayeredIcon(1)
            decorated.setIcon(icon, 0, 0, 0)
            //decorated.setIcon(badgeIcon, 1,0,0)
            return decorated
        }
}