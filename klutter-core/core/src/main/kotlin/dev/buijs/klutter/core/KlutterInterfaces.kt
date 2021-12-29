package dev.buijs.klutter.core

import dev.buijs.klutter.core.log.KlutterLogger
import java.io.File

 /** Utility interface which takes (generated) file content and writes it to a file.
 * Used for editting/creating classes and configuration files.
 */
interface KlutterWriter { fun write(file: File, content: String) }

/**
 * Utility interface which edits or generates file content.
 */
interface KlutterPrinter { fun print(): String }

/**
 * Utility interface which uses the KlutterPrinter and KlutterWriter for code generation.
 */
interface KlutterCodeGenerator { fun generate(): KlutterLogger }