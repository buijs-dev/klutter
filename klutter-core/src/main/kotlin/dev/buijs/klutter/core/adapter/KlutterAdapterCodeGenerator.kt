package dev.buijs.klutter.core.adapter

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.log.KlutterLogging
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import java.io.File

/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
class KlutterAdapterCodeGenerator(
    val context: Project,
    val sources: List<File>,
    val android: File,
    val flutter: File,
    val podspec: File) {

    private var logger = KlutterLogging()

    fun generate(): KlutterLogging {

        val scannedSources = scanSources(sources)
            .filter { it.content.contains("@KlutterAdaptee") }

        if(scannedSources.isEmpty()){
            logger.warn("None of the files contain @KlutterAdaptee annotation.")
        }

        val methods = scannedSources
            .map { convertToKotlinFiles(it) }
            .map { convertToMethodCallDefinitions(it) }

        methods
            .map { KlutterAndroidAdapterPrinter().print(it) }
            .forEach { KlutterAndroidAdapterWriter().write(android, it)}

        val activityFile = scanSources(listOf(android))
            .filter { it.content.contains("@KlutterAdapter") }

        if(activityFile.isEmpty()){
            throw KlutterCodeGenerationException("MainActivity not found or  the @KlutterAdapter is missing in folder $android.")
        }

        activityFile
            .map { convertToKotlinFiles(it) }
            .map { KlutterActivityPrinter().print(it) }
            .forEach { KlutterActivityWriter().write(it.file, it.content)}

        val mainDartFile = findMainDartFile(flutter)
        val adapterBody = KlutterFlutterAdapterPrinter().print(methods.flatten())
        KlutterFlutterAdapterWriter().write(mainDartFile.parentFile, adapterBody)

        KlutterIosAdapterWriter().write(podspec)

        return logger
    }

    private fun scanSources(directories: List<File>): List<FileContent> {
        val classes = mutableListOf<FileContent>()

        directories.forEach { directory ->
            logger.debug("Scanning for files in directory '$directory'")
            if (directory.exists()) {
                directory.walkTopDown().forEach { f ->
                    if(f.isFile) {
                        logger.debug("Found file '$f' in directory '$directory'")
                        classes.add(FileContent(file = f, content = f.readText()))
                    }
                }
            } else logger.error("Failed to scan directory because it does not exist: '$directory'")
        }

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
                            defintions.addAll(KtFileScanner(
                                clazz.fqName?.asString(),
                                clazz.name?:"",
                                it.text).scan())
                            logger.info("Found @KlutterAdaptee annotation in KtClass '${clazz.name}'")
                        }
                    }
                }
            }
        }
        return defintions
    }

    private fun findMainDartFile(directory: File): File {
        logger.debug("Scanning for main.dart in directory '$directory'")
        if (directory.exists()) {
            directory.walkTopDown().forEach { f ->
                logger.debug("Found file '$f' with name ${f.name} and extenions ${f.extension}")
                if(f.isFile && f.name == "main.dart"){
                    logger.debug("Found main.dart file in directory '$f''")
                    return f
                }
            }
            throw KlutterCodeGenerationException("Could not find main.dart in directory: '$directory'")
        }
        throw KlutterCodeGenerationException("Could not find main.dart because directory does not exist: '$directory'")
    }

}