package dev.buijs.klutter.jetbrains.shared

import com.intellij.openapi.module.ModuleType

class KlutterModuleType : ModuleType<AbstractKlutterModuleBuilder>("KLUTTER_MODULE") {

    override fun createModuleBuilder() = KlutterModuleBuilder()

    override fun getName() = "Buijs Software Klutter Framework"

    override fun getDescription() = "Add support for the Klutter Framework"

    override fun getNodeIcon(isOpened: Boolean) = KlutterIcons.logo16x16

}