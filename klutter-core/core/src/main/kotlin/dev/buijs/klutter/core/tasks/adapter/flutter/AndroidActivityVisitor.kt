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

package dev.buijs.klutter.core.tasks.adapter.flutter

import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.KlutterVisitor
import dev.buijs.klutter.core.KtFileContent
import java.io.File

private const val generatedAdapterImportLine = "import dev.buijs.klutter.adapter.GeneratedKlutterAdapter"
private const val flutterActivityImportLine = "import io.flutter.embedding.android.FlutterActivity"
private const val androidNonNullImportLine = "import androidx.annotation.NonNull"
private const val flutterEngineImportLine = "import io.flutter.embedding.engine.FlutterEngine"
private const val generatedPluginRegImportLine = "import io.flutter.plugins.GeneratedPluginRegistrant"
private const val methodChannelImportLine = "import io.flutter.plugin.common.MethodChannel"
private const val methodChannelFunLine1 = """    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {"""
private const val methodChannelFunLine2 = """        MethodChannel(flutterEngine.dartExecutor,"KLUTTER")"""
private const val methodChannelFunLine3 = """            .setMethodCallHandler{ call, result ->"""
private const val methodChannelFunLine4 = """                GeneratedKlutterAdapter().handleMethodCalls(call, result)"""
private const val methodChannelFunLine5 = """            }"""
private const val methodChannelFunLine6 = """        GeneratedPluginRegistrant.registerWith(flutterEngine)"""
private const val methodChannelFunLine7 = """    }"""

/**
 * Class which may or may not edit the MainActivity file in the android/app folder
 * to add platform method channel boilerplate code which delegates any communication
 * between Flutter and KMP to the GeneratedKlutterAdapter class.
 *
 * @author Gillian Buijs
 */
internal class AndroidActivityVisitor(
    private val metaFile: KtFileContent
): KlutterVisitor {

    override fun visit() {
        val source = filteredSourceLines(metaFile)
        val output = mutableListOf<String>()

        source.forEach { line -> output.add(line) }

        var packageLine: Int? = null
        var importsStartingLine: Int? = null
        var configureFlutterEngineLine: Int? = null
        var classDeclarationLine: Int? = null
        var containsMethodChannelImport = false
        var containsGeneratedAdapterImport = false
        var containsFlutterActivityImport = false
        var containsAndroidNonNullImport = false
        var containsFlutterEngineImport = false

        source.forEachIndexed { index, line ->
            when {
                line.startsWith("package ") -> {
                    packageLine = index
                }

                line.startsWith("import ") -> {
                    importsStartingLine = importsStartingLine ?: index

                    when {
                        line.contains(methodChannelImportLine) -> {
                            containsMethodChannelImport = true
                        }
                        line.contains(generatedAdapterImportLine) -> {
                            containsGeneratedAdapterImport = true
                        }
                        line.contains(flutterActivityImportLine) -> {
                            containsFlutterActivityImport = true
                        }
                        line.contains(androidNonNullImportLine) -> {
                            containsAndroidNonNullImport = true
                        }
                        line.contains(flutterEngineImportLine) -> {
                            containsFlutterEngineImport = true
                        }
                    }
                }

                line.contains("fun configureFlutterEngine") -> {
                    configureFlutterEngineLine = index + 1
                }

                line.contains("class MainActivity") -> {
                    classDeclarationLine = index + 1
                }
            }
        }

        if(packageLine == null) {
            throw KlutterCodeGenerationException("""
                Could not determine package name for class containing @KlutterAdaptor annotation.
                Aborting code generation because this likely indicates a problem.
                Please check the KlutterPlugin configuration in the root build.gradle(.kts)
                and verify the paths pointing to the flutter/android/app folder.
                Also verify if your Flutter project has no issues.
                """.trimIndent())
        }

        if(importsStartingLine == null) {
            throw KlutterCodeGenerationException("""
                No import statements found in class containing @KlutterAdapter annotation.
                Aborting code generation because this likely indicates a problem.
                A MainActivity class should at least extend FlutterActivity which requires an import.
                Please check the KlutterPlugin configuration in the root build.gradle(.kts)
                and verify the paths pointing to the flutter/android/app folder.
                Also verify if your Flutter project has no issues.
                """.trimIndent())
        }

        if(classDeclarationLine == null) {
            throw KlutterCodeGenerationException("""
                Could not find MainActivity!
                Aborting code generation because this likely indicates a problem.
                Please check the KlutterPlugin configuration in the root build.gradle(.kts)
                and verify the paths pointing to the flutter/android/app folder.
                Also verify if your Flutter project has no issues.
                """.trimIndent())
        }

        if(configureFlutterEngineLine == null) {
            if(!containsMethodChannelImport){
                output.add(importsStartingLine!!, methodChannelImportLine)
                classDeclarationLine = classDeclarationLine!! + 1
            }

            if(!containsGeneratedAdapterImport){
                output.add(importsStartingLine!!, generatedAdapterImportLine)
                classDeclarationLine = classDeclarationLine!! + 1
            }

            if(!containsAndroidNonNullImport){
                output.add(importsStartingLine!!, androidNonNullImportLine)
                classDeclarationLine = classDeclarationLine!! + 1
            }

            if(!containsFlutterActivityImport){
                output.add(importsStartingLine!!, flutterActivityImportLine)
                classDeclarationLine = classDeclarationLine!! + 1
            }

            if(!containsFlutterEngineImport){
                output.add(importsStartingLine!!, flutterEngineImportLine)
                classDeclarationLine = classDeclarationLine!! + 1
            }

            output.add(importsStartingLine!!, generatedPluginRegImportLine)
            classDeclarationLine = classDeclarationLine!! + 1

            output.add((classDeclarationLine!! + 1), methodChannelFunLine1)
            output.add((classDeclarationLine!! + 2), methodChannelFunLine2)
            output.add((classDeclarationLine!! + 3), methodChannelFunLine3)
            output.add((classDeclarationLine!! + 4), methodChannelFunLine4)
            output.add((classDeclarationLine!! + 5), methodChannelFunLine5)
            output.add((classDeclarationLine!! + 6), methodChannelFunLine6)
            output.add((classDeclarationLine!! + 7), "$methodChannelFunLine7\r\n")
        } else {

            if(!containsMethodChannelImport){
                output.add(importsStartingLine!!, methodChannelImportLine)
                configureFlutterEngineLine = configureFlutterEngineLine!! + 1
            }

            if(!containsGeneratedAdapterImport){
                output.add(importsStartingLine!!, generatedAdapterImportLine)
                configureFlutterEngineLine = configureFlutterEngineLine!! + 1
            }

            output.add((configureFlutterEngineLine!!), methodChannelFunLine2)
            output.add((configureFlutterEngineLine!! + 1), methodChannelFunLine3)
            output.add((configureFlutterEngineLine!! + 2), methodChannelFunLine4)
            output.add((configureFlutterEngineLine!! + 3), methodChannelFunLine5)
        }

        write(file = metaFile.file, classBody = output.joinToString("\r\n"))
    }

    private fun filteredSourceLines(metaFile: KtFileContent): List<String> {

        val source = metaFile.content.reader().readLines()
        var indexOfMethodChannelHandler = -1
        source.forEachIndexed { index, it ->
            if (it.filter { !it.isWhitespace() } == methodChannelFunLine2.filter { !it.isWhitespace() }) {
                indexOfMethodChannelHandler = index
            }
        }

        return if (indexOfMethodChannelHandler == -1) { source } else {
            val indexRange = IntRange(indexOfMethodChannelHandler, (indexOfMethodChannelHandler + 3))
            val temp = mutableListOf<String>()
            source.forEachIndexed { index, it -> if (!indexRange.contains(index)) { temp.add(it) } }
            temp
        }
    }

    private fun write(file: File, classBody: String) {
        if(!file.exists()){
            throw KlutterCodeGenerationException(
                "The given path to the MainActivity file in the android/app folder does not exist.\r\n" +
                        "Make sure the given path in the KlutterPlugin is an absolute path pointing " +
                        "to the android/app folder in the flutter root project.")

        } else file.delete()

        file.createNewFile()
        file.writeText(classBody)
    }

}