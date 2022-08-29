package dev.buijs.klutter.jetbrains.shared

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import dev.buijs.klutter.jetbrains.intellij.NewKlutterProjectWizard
import dev.buijs.klutter.jetbrains.studio.NewKlutterProjectWizardLegacy
import dev.buijs.klutter.kore.shared.ClassFileLoader

class KlutterModuleBuilder: AbstractKlutterModuleBuilder() {

    init {
        addListener(this)
    }

    override fun getCustomOptionsStep(
        context: WizardContext?,
        parentDisposable: Disposable?,
    ): ModuleWizardStep {
        val deps = getLoadedPackageVersions("java.", "javax.", "jdk.", "sun.", "com.sun.")
        println(deps)

        val newKotlinDslClass = "com.intellij.openapi.observable.properties.ObservableMutableProperty"
        val isNewDsl = ClassFileLoader.tryLoadClass(newKotlinDslClass) != null

        return if(isNewDsl) {
            NewKlutterProjectWizard(this)
        } else {
            NewKlutterProjectWizardLegacy(this)
        }

    }

    override fun getModuleType(): ModuleType<*> {
        return KlutterModuleType()
    }

}