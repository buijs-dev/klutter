package dev.buijs.klutter.core.adapter

import dev.buijs.klutter.core.KlutterCodeGenerationException


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
private const val generatedAdapterImportLine = "import dev.buijs.klutter.adapter.GeneratedKlutterAdapter"
private const val methodChannelImportLine = "import io.flutter.plugin.common.MethodChannel"
private const val methodChannelFunLine1 = """        MethodChannel(flutterEngine.dartExecutor,"KLUTTER")"""
private const val methodChannelFunLine2 = """            .setMethodCallHandler{ call, result ->"""
private const val methodChannelFunLine3 = """                GeneratedKlutterAdapter().handleMethodCalls(call, result)"""
private const val methodChannelFunLine4 = """            }"""

internal class KlutterActivityPrinter {

    fun print(metaFile: KtFileContent): FileContent {

        val source = filteredSourceLines(metaFile)
        val output = mutableListOf<String>()

        source.forEach { line -> output.add(line) }

        var packageLine: Int? = null
        var importsStartingLine: Int? = null
        var configureFlutterEngineLine: Int? = null
        var containsMethodChannelImport = false
        var containsGeneratedAdapterImport = false

        source.forEachIndexed { index, line ->
            when {
                line.startsWith("package ") -> {
                    packageLine = index
                }

                line.startsWith("import ") -> {
                    importsStartingLine = importsStartingLine ?: index

                    if(line.contains(methodChannelImportLine)){
                        containsMethodChannelImport = true
                    } else if(line.contains(generatedAdapterImportLine)){
                        containsGeneratedAdapterImport = true
                    }
                }

                line.contains("fun configureFlutterEngine") -> {
                    configureFlutterEngineLine = index + 1
                }

            }
        }

        if(packageLine == null) {
            throw KlutterCodeGenerationException("""
                Could not determine package name for class containing @KlutterAdaptor annotation.
                Aborting code generation because this likely indicates a problem.
                Please check the KlutterAdapterPlugin configuration in the root build.gradle(.kts)
                and verify the paths pointing to the flutter/android/app folder.
                Also verify if your Flutter project has no issues.
                """.trimIndent())
        }

        if(importsStartingLine == null) {
            throw KlutterCodeGenerationException("""
                No import statements found in class containing @KlutterAdapter annotation.
                Aborting code generation because this likely indicates a problem.
                A MainActivity class should at least extend FlutterActivity which requires an import.
                Please check the KlutterAdapterPlugin configuration in the root build.gradle(.kts)
                and verify the paths pointing to the flutter/android/app folder.
                Also verify if your Flutter project has no issues.
                """.trimIndent())
        }

        if(configureFlutterEngineLine == null) {
            throw KlutterCodeGenerationException("""
                Could not find a function in the MainActivity which has the name "configureFlutterEngine".
                Aborting code generation because this likely indicates a problem.
                Please check the KlutterAdapterPlugin configuration in the root build.gradle(.kts)
                and verify the paths pointing to the flutter/android/app folder.
                Also verify if your Flutter project has no issues.
                """.trimIndent())
        }

        if(!containsMethodChannelImport){
            output.add(importsStartingLine!!, methodChannelImportLine)
            configureFlutterEngineLine = configureFlutterEngineLine!! + 1
        }

        if(!containsGeneratedAdapterImport){
            output.add(importsStartingLine!!, generatedAdapterImportLine)
            configureFlutterEngineLine = configureFlutterEngineLine!! + 1
        }

        output.add((configureFlutterEngineLine!!), methodChannelFunLine1)
        output.add((configureFlutterEngineLine!! + 1), methodChannelFunLine2)
        output.add((configureFlutterEngineLine!! + 2), methodChannelFunLine3)
        output.add((configureFlutterEngineLine!! + 3), methodChannelFunLine4)

        return FileContent(file = metaFile.file, content = output.joinToString("\r\n"))
    }

    private fun filteredSourceLines(metaFile: KtFileContent): List<String> {

        val source = metaFile.content.reader().readLines()
        var indexOfMethodChannelHandler = -1
        source.forEachIndexed { index, it ->
            if (it.filter { !it.isWhitespace() } == methodChannelFunLine1.filter { !it.isWhitespace() }) {
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
}