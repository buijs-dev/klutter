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
import dev.buijs.klutter.annotations.processor.KtFileScanner
import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.FileContent
import dev.buijs.klutter.core.KtFileContent
import dev.buijs.klutter.core.MethodCallDefinition
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.*
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidActivityVisitor
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidAdapterGenerator
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidBuildGradleGenerator
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidRootBuildGradleGenerator
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.FlutterAdapterGenerator
import dev.buijs.klutter.plugins.gradle.tasks.adapter.kmp.IosPodspecVisitor
import dev.buijs.klutter.plugins.gradle.tasks.adapter.protobuf.ProtoGenerator
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import java.io.File

/**
 * @author Gillian Buijs
 */
@Suppress("unused")
class KlutterAdapterProducer(
    private val context: Project,
    private val project: KlutterProject,
    private val iosVersion: String,
    private val protoObjects: ProtoObjects
): KlutterProducer
{

    private var logger = KlutterLogger()

    override fun produce(): KlutterLogger {
        val root = project.root
        val flutter = project.flutter
        val android = project.android
        val ios = project.ios
        val kmp = project.kmp
        val klutter = project.klutter
        val podspec = project.kmp.podspec()
        val methods = scanForAdaptees()
        val androidAdapterGenerator = AndroidAdapterGenerator(methods, android.app())
        val androidActivityVisitor = AndroidActivityVisitor(findAndroidActivity(android))
        val flutterAdapterGenerator = FlutterAdapterGenerator(flutter, methods)
        val androidBuildGradleGenerator = AndroidBuildGradleGenerator(root, android.app())
        val androidRootBuildGradleGenerator = AndroidRootBuildGradleGenerator(root, android)
        val androidManifestVisitor = AndroidManifestVisitor(android.manifest())
        val iosAppDelegateGenerator = IosAppDelegateGenerator(methods, ios, podspec.nameWithoutExtension)

        val iosPodspecVisitor = IosPodspecVisitor(podspec)

        val iosPodFileGenerator = IosPodFileGenerator(
            iosVersion = iosVersion,
            ios = project.ios,
            kmp = kmp,
            podName = podspec.nameWithoutExtension
        )

        val protoGenerator = ProtoGenerator(klutter, protoObjects)

        return logger
            .merge(protoGenerator.generate())
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

    private fun scanForAdaptees(): List<MethodCallDefinition> {
        val scannedSources = scanSources(project.kmp.source())
            .filter { it.content.contains("@KlutterAdaptee") }

        if(scannedSources.isEmpty()){
            logger.warn("None of the files contain @KlutterAdaptee annotation.")
        }

        return scannedSources
            .map { convertToKotlinFiles(it) }
            .map { convertToMethodCallDefinitions(it) }
            .flatten()
    }

    private fun findAndroidActivity(android: Android): KtFileContent {
        val appDir = android.app()
        val activityFile = scanSources(appDir)
            .filter { it.content.contains("@KlutterAdapter") }

        if(activityFile.isEmpty()){
            throw KlutterCodeGenerationException("MainActivity not found or  the @KlutterAdapter is missing in folder $appDir.")
        }

        if(activityFile.size > 1) {
            throw KlutterCodeGenerationException(
                "Expected to find one @KlutterAdapter annotation in the MainActivity file but found ${activityFile.size} files."
            )
        }

        return convertToKotlinFiles(activityFile[0])
    }

    private fun scanSources(directory: File): List<FileContent> {
        val classes = mutableListOf<FileContent>()

        logger.debug("Scanning for files in directory '$directory'")
        if (directory.exists()) {
            directory.walkTopDown().forEach { f ->
                if(f.isFile) {
                    logger.debug("Found file '$f' in directory '$directory'")
                    classes.add(FileContent(file = f, content = f.readText()))
                }
            }
        } else logger.error("Failed to scan directory because it does not exist: '$directory'")

        return classes
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

    private fun convertToMethodCallDefinitions(ktFileContent: KtFileContent): List<MethodCallDefinition> {
        val defintions = mutableListOf<MethodCallDefinition>()

        ktFileContent.ktFile.children.forEach { clazz ->
            if (clazz is KtClass) {
                logger.debug("Found a KtClass: '${clazz.name}'")

                clazz.allChildren.forEach {
                    if(it is KtClassBody){
                        logger.debug("Scanning KtClass '${clazz.name}' for @KlutterAdaptee annotation")
                        if(it.text.contains("@KlutterAdaptee")){
                            val scanned = KtFileScanner(clazz.fqName?.asString(), clazz.name?:"", it.text).scan()

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