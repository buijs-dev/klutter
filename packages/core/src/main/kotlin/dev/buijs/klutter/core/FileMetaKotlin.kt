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

package dev.buijs.klutter.core

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import java.io.File

/**
 * @author Gillian Buijs
 */
internal data class MethodCallDefinition(
    val getter: String,
    val import: String,
    val call: String,
    val async: Boolean = false,
    val returns: String,
)

/**
 * @author Gillian Buijs
 */
internal data class MethodData(
    val getter: String,
    val methodCall: String,
)

/**
 * @author Gillian Buijs
 */
internal data class FileContent(
    val file: File,
    val content: String,
)

/**
 * @author Gillian Buijs
 */
internal data class KtFileContent(
    val file: File,
    val ktFile: KtFile,
    val content: String,
) {

    internal fun toMethodCallDefinition() =
        ktFile.children.filterIsInstance<KtClass>().flatMap { c ->
            c.allChildren.mapNotNull {
                if (it.hasAdapteeAnnotation()) {
                    c.toMethodCallDefinitions(it.text).also { scanned ->
                        scanned.ifEmpty { throw exception }
                    }
                } else null
            }
        }.flatten()

    private val exception = KlutterCodeGenerationException(
        """
        Scanning KtFile failed. Please check if the @KlutterAdaptee annotation is used correctly.
        It should be placed on a function and have a name. 
        
        Example:
        
        @KlutterAdaptee(name = "fooBar")
        fun someFoo(): NotBar {
            return "foo!"
        }
        
        """.trimIndent()
    )

    private fun PsiElement.hasAdapteeAnnotation() =
        this is KtClassBody && text.contains("@KlutterAdaptee")
}

internal fun FileContent.toKotlinFiles(context: Project): KtFileContent {
    val psi = PsiManager.getInstance(context)

    if(!file.exists()){
        throw KlutterCodeGenerationException("Source file does not exist: ${file.absolutePath}")
    }

    val ktFile = psi.findFile(
        LightVirtualFile(file.name, KotlinFileType.INSTANCE, content)
    ) as KtFile

    return KtFileContent(file = file, ktFile = ktFile, content = ktFile.text)
}


private fun KtClass.toMethodCallDefinitions(ktFileBody: String): List<MethodCallDefinition> {

    val fqdn = this.fqName?.asString()
    val className = this.name?:""
    val packagename = """package(.*)""".toRegex()
        .find(ktFileBody)
        ?.value
        ?.filter { !it.isWhitespace() }
        ?:""

    val trimmedBody = ktFileBody.filter { !it.isWhitespace() }

    return """@KlutterAdaptee\(("|[^"]+?")([^"]+?)".+?(suspend|)fun([^(]+?\([^:]+?):([^{]+?)\{""".toRegex()
        .findAll(trimmedBody).map { match ->
            val getter = match.groups[2]?.value?:throw KotlinFileScanningException("""
                   Unable to process KlutterAdaptee annotation.
                   Please make sure the annotation is as follows: 'klutterAdaptee(name = "foo")'
                   """.trim())

            val caller = match.groups[4]?.value?:throw KotlinFileScanningException("""
                   Unable to process KlutterAdaptee annotation.
                   Please make sure the annotation is as follows: 'klutterAdaptee(name = "foo")'
                   """.trim())

            val returns = match.groups[5]?.value?.trim()?:throw KotlinFileScanningException("""
                    Unable to determine return type of function annotated with @KlutterAdaptee.
                    The method signature should be as follows: fun foo(): Bar { //your implementation }
                   """.trim())

            MethodCallDefinition(
                import = fqdn?:packagename,
                getter = getter,
                call = "$className().$caller",
                async = match.groups[3]?.value?.trim()?.isNotBlank()?:false,
                returns = returns)

        }.toList()
}