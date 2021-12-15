package dev.buijs.klutter.core.adapter

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import dev.buijs.klutter.annotations.KlutterAdaptee
import dev.buijs.klutter.core.KlutterCodeGenerationException
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.utils.addToStdlib.cast
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
    val flutter: File) {

    fun generate() {

        scanSources(sources)
            .filter { it.content.contains("@KlutterAdaptee") }
            .asSequence()
            .map { convertToKotlinFiles(it) }
            .map { convertToMethodCallDefinitions(it) }
            .map { KlutterAdapterPrinter().print(it) }
            .forEach { KlutterAdapterWriter().write(android, it)}


        val activityFile = scanSources(listOf(android))
            .filter { it.content.contains("@KlutterAdapter") }

        if(activityFile.isEmpty()){
            throw KlutterCodeGenerationException("MainActivity not found or  the @KlutterAdapter is missing in folder $android.")
        }

        activityFile
            .map { convertToKotlinFiles(it) }
            .map { KlutterActivityPrinter().print(it) }
            .forEach { KlutterActivityWriter().write(it.file, it.content)}

    }


    private fun scanSources(directories: List<File>): List<FileContent> {
        val classes = mutableListOf<FileContent>()

        directories.forEach { directory ->
            if (directory.exists()) {
                directory.walkTopDown().forEach { f ->
                    if(f.isFile) {
                        classes.add(FileContent(file = f, content = f.readText()))
                    }
                }
            }
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
                clazz.allChildren.forEach {
                    if(it is KtClassBody){
                        if(it.text.contains(KlutterAdaptee::class.simpleName.toString())){
                            defintions.addAll(KtFileScanner(it.text).scan())
                        }
                    }
                }
            }
        }

        return defintions
    }

}