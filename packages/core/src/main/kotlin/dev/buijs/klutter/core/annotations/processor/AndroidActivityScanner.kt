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

package dev.buijs.klutter.core.annotations.processor

import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import dev.buijs.klutter.core.*
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import com.intellij.openapi.project.Project

internal class AndroidActivityScanner(
    private val context: Project,
    private val android: Android,
) {

    internal fun scan(): KtFileContent {
        val activityFile = KlutterAnnotatedSourceCollector(android.app(), "@KlutterAdapter").collect()

        if(activityFile.isEmpty()){
            throw KlutterCodeGenerationException("MainActivity not found or  the @KlutterAdapter is missing in folder ${android.app()}.")
        }

        if(activityFile.size > 1) {
            throw KlutterCodeGenerationException(
                "Expected to find one @KlutterAdapter annotation in the MainActivity file but found ${activityFile.size} files."
            )
        }

        return convertToKotlinFiles(activityFile[0])
    }

    private fun convertToKotlinFiles(source: FileContent): KtFileContent {
        val psi = PsiManager.getInstance(context)

        if(!source.file.exists()){
            throw KlutterCodeGenerationException("Source file does not exist: ${source.file.absolutePath}")
        }

        val ktFile = psi.findFile(
            LightVirtualFile(source.file.name, KotlinFileType.INSTANCE, source.content)
        ) as KtFile

        return KtFileContent(file = source.file, ktFile = ktFile, content = ktFile.text)
    }

}
