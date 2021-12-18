package dev.buijs.klutter.core.adapter

import dev.buijs.klutter.core.KotlinFileScanningException

/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
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