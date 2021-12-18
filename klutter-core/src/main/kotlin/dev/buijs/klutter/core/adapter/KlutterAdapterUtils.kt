package dev.buijs.klutter.core.adapter

import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
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