package dev.buijs.klutter.core

import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class KtFileScanner(
    private val fqdn: String?,
    private val className: String,
    private val ktFileBody: String) {

    fun scan(): List<MethodCallDefinition> {

        val methods = mutableListOf<MethodData>()

        val packagename = """package(.*)""".toRegex()
            .find(ktFileBody)
            ?.value
            ?.filter { !it.isWhitespace() }
            ?:""

        val trimmedBody = ktFileBody.filter { !it.isWhitespace() }

        """@KlutterAdaptee\(name="([^"]+?)"\)fun([^:]+?):""".toRegex()
            .findAll(trimmedBody).forEach { match ->
                methods.add(
                    MethodData(
                    getter = match.groups[1]?.value?:throw KotlinFileScanningException("""
                       Unable to process KlutterAdaptee annotation.
                       Please make sure the annotation is as follows: 'klutterAdaptee(name = "foo")'
                       """.trim()),
                    methodCall = match.groups[2]?.value?:throw KotlinFileScanningException("""
                       Unable to process KlutterAdaptee annotation.
                       Please make sure the annotation is as follows: 'klutterAdaptee(name = "foo")'
                       """.trim()),
                    )
                )
            }

        return methods.map {
            MethodCallDefinition(
                call = "$className().${it.methodCall}",
                import = fqdn?:packagename,
                returns = Any::class.java,
                getter = it.getter
            )
        }
    }

}

internal data class MethodCallDefinition(
    val getter: String,
    val import: String,
    val call: String,
    val returns: Class<*>)

internal data class MethodData(
    val getter: String,
    val methodCall: String)

internal data class FileContent(
    val file: File,
    val content: String)

internal data class KtFileContent(
    val file: File,
    val ktFile: KtFile,
    val content: String)