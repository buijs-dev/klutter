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

package dev.buijs.klutter.plugins.gradle.tasks.adapter


import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import dev.buijs.klutter.annotations.processor.KlutterAdapteeScanner
import dev.buijs.klutter.annotations.processor.KlutterResponseScanResult
import dev.buijs.klutter.annotations.processor.KlutterResponseScanner
import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.FileContent
import dev.buijs.klutter.core.KtFileContent
import dev.buijs.klutter.core.MethodCallDefinition
import dev.buijs.klutter.plugins.gradle.dsl.KlutterRepository
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.*
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidActivityVisitor
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidAdapterGenerator
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidBuildGradleGenerator
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidRootBuildGradleGenerator
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.FlutterAdapterGenerator
import dev.buijs.klutter.plugins.gradle.tasks.adapter.kmp.IosPodspecVisitor
import dev.buijs.klutter.plugins.gradle.tasks.adapter.dart.DartGenerator
import dev.buijs.klutter.plugins.gradle.utils.AnnotatedSourceCollector
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.allChildren

/**
 * @author Gillian Buijs
 */
@Suppress("unused")
class KlutterAdapterProducer(
    private val context: Project,
    private val project: KlutterProject,
    private val iosVersion: String,
    private val repositories: List<KlutterRepository>,
): KlutterProducer
{

    private var logger = KlutterLogger()

    //todo complexity
    override fun produce(): KlutterLogger {
        val root = project.root
        val flutter = project.flutter
        val android = project.android
        val ios = project.ios
        val kmp = project.kmp
        val podspec = project.kmp.podspec()
        val methods = scanForAdaptees()
        val androidAdapterGenerator = AndroidAdapterGenerator(methods, android.app())
        val androidActivityVisitor = AndroidActivityVisitor(scanForAndroidActivity(android))
        val flutterAdapterGenerator = FlutterAdapterGenerator(flutter, methods)
        val androidBuildGradleGenerator = AndroidBuildGradleGenerator(root, android.app())
        val androidRootBuildGradleGenerator = AndroidRootBuildGradleGenerator(root, android, repositories)
        val androidManifestVisitor = AndroidManifestVisitor(android.manifest())
        val iosAppDelegateGenerator = IosAppDelegateGenerator(methods, ios, podspec.nameWithoutExtension)

        val iosPodspecVisitor = IosPodspecVisitor(podspec)

        val iosPodFileGenerator = IosPodFileGenerator(
            iosVersion = iosVersion,
            ios = project.ios,
            kmp = kmp,
            podName = podspec.nameWithoutExtension
        )

        val dartObjects = scanForResponses()
        val dartGenerator = DartGenerator(flutter, dartObjects.dart)

        return logger
            .merge(dartGenerator.generate())
            .merge(androidAdapterGenerator.generate())
            .merge(androidActivityVisitor.visit())
            .merge(flutterAdapterGenerator.generate())
            .merge(androidBuildGradleGenerator.generate())
            .merge(androidRootBuildGradleGenerator.generate())
            .merge(androidManifestVisitor.visit())
            .merge(iosAppDelegateGenerator.generate())
            .merge(iosPodspecVisitor.visit())
            .merge(iosPodFileGenerator.generate())
    }

    //todo does not belong here and too big
    private fun scanForResponses(): KlutterResponseScanResult {

        val enumerations = mutableListOf<DartEnum>()
        val messages = mutableListOf<DartMessage>()

        AnnotatedSourceCollector(project.kmp.source(), "@KlutterResponse")
            .collect()
            .also { logger.merge(it.logger) }
            .collection
            .map { convertToKotlinFiles(it) }
            .map { KlutterResponseScanner(it.content).scan() }
            .forEach {
                enumerations.addAll(it.dart.enumerations)
                messages.addAll(it.dart.messages)
            }

        val customDataTypes = mutableListOf<String>()

        //Collect all custom data types
        for (message in messages) {
            for (field in message.fields) {
                field.customDataType?.let { customDataTypes.add(it) }
            }
        }

        //Iterate the message names and match it to the custom data types.
        //Remove from custom data types list if matched.
        messages.map { it.name }.forEach { customDataTypes.remove(it) }

        //Iterate the enumeration names and match it to the custom data types.
        //Remove from custom data types list if matched.
        enumerations.map { it.name }.forEach { customDataTypes.remove(it) }

        //Any custom data type name left in the list means there is no class definition found by this name
        messages.removeIf { message ->
            message.fields.map { field -> field.name }.any { customDataTypes.contains(it) }
        }

        if(customDataTypes.isNotEmpty()) {
            throw KlutterCodeGenerationException(
                """ |Processing annotation '@KlutterResponse' failed, caused by:
                    |
                    |Could not resolve all class names.
                    |
                    |Verify if all KlutterResponse annotated classes comply with the following rules:
                    |
                    |1. Must be an open class
                    |2. Fields must be immutable
                    |3. Constructor only (no body)
                    |4. No inheritance
                    |5. Any field type should comply with the same rules
                    |
                    |If this looks like a bug please file an issue at: https://github.com/buijs-dev/klutter/issues
                """.trimMargin())
        }

        return KlutterResponseScanResult(
            dart = DartObjects(messages, enumerations),
        )

    }

    private fun scanForAdaptees(): List<MethodCallDefinition> {
        val sources = AnnotatedSourceCollector(project.kmp.source(), "@KlutterAdaptee")
            .collect()
            .also { logger.merge(it.logger) }

        return sources.collection
            .map { convertToKotlinFiles(it) }
            .map { convertToMethodCallDefinitions(it) }
            .flatten()
    }

    private fun scanForAndroidActivity(android: Android): KtFileContent {
        val activityFile = AnnotatedSourceCollector(android.app(), "@KlutterAdapter")
            .collect()
            .also { logger.merge(it.logger) }
            .collection

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

    //todo  does not belong here
    private fun convertToMethodCallDefinitions(ktFileContent: KtFileContent): List<MethodCallDefinition> {
        val defintions = mutableListOf<MethodCallDefinition>()

        ktFileContent.ktFile.children.forEach { clazz ->
            if (clazz is KtClass) {
                logger.debug("Found a KtClass: '${clazz.name}'")

                clazz.allChildren.forEach {
                    if(it is KtClassBody){
                        logger.debug("Scanning KtClass '${clazz.name}' for @KlutterAdaptee annotation")
                        if(it.text.contains("@KlutterAdaptee")){
                            val scanned = KlutterAdapteeScanner(clazz.fqName?.asString(), clazz.name?:"", it.text).scan()

                            if(scanned.isEmpty()){
                                logger.error("""
                                    Scanning KtFile failed. Please check if the @KlutterAdaptee annotation is used correctly.
                                    It should be placed on a function and have a name. 
                                    
                                    Example:
                                    
                                    @KlutterAdaptee(name = "fooBar")
                                    fun someFoo(): NotBar {
                                        return "foo!"
                                    }
                                    
                                    """.trimIndent())
                            }

                            defintions.addAll(scanned)
                            logger.info("Found @KlutterAdaptee annotation in KtClass '${clazz.name}'")
                        }
                    }
                }
            }
        }
        return defintions
    }
}