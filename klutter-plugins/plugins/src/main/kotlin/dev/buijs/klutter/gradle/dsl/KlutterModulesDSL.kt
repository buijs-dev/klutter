package dev.buijs.klutter.gradle.dsl

import java.io.File
import java.nio.file.Path


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
 */
@DslMarker
internal annotation class ModulesDSLMarker

@ModulesDSLMarker
class KlutterModulesDSL(private val projectRoot: File): KlutterDSL<KlutterModulesBuilder> {
    override fun configure(lambda: KlutterModulesBuilder.() -> Unit): KlutterModulesDTO {
        return KlutterModulesBuilder(projectRoot).apply(lambda).build()
    }
}

@ModulesDSLMarker
class KlutterModulesBuilder(projectRoot: File): KlutterDSLBuilder {

    private val modules = mutableListOf<KlutterModule>()
    private var root = projectRoot

    fun root(path: String, absolute: Boolean = false) {
        val isRelative = !absolute
        root = if(isRelative) {
            RelativeRoot(root, path).file()
        } else {
            AbsoluteRoot(path).file()
        }
    }

    fun module(to: String, absolute: Boolean = false) {
        modules.add(KlutterModule(path = to, absolute = absolute))
    }

    override fun build() = KlutterModulesDTO(modules = modules.map { printModulePath(it) })

    private fun printModulePath(module: KlutterModule): String {
        val isNotAbsolute = !module.absolute
        val path = module.path
        return if(isNotAbsolute) root.resolve(path).normalize().absolutePath else path
    }

}

internal data class KlutterModule(
    val path: String,
    val absolute: Boolean
)

internal class RelativeRoot(
    private val root: File,
    private val path: String)
{
    fun file(): File = root.resolve(path).normalize()
}

internal class AbsoluteRoot(
    private val path: String)
{
    fun file(): File = Path.of("").toAbsolutePath().root.resolve(path).toFile()
}

/**
 * DTO for storing Kotlin Modules configuration.
 * Used by adapter plugin to find the common sourcecode, .aar file for android and podspec for ios.
 */
data class KlutterModulesDTO(val modules: List<String>): KlutterDTO
