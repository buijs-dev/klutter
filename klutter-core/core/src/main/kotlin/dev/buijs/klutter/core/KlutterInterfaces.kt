package dev.buijs.klutter.core

/**
 * The purpose of this class is to create new files.
 * A KlutterFileGenerator uses a [KlutterPrinter] and a [KlutterWriter]
 * to encapsulate the functionality for creating the content of a file
 * and actually writing the content to a file location.
 */
abstract class KlutterFileGenerator {

 /**
  * Use the [printer] function to create the file content
  * and pass it to the [writer] fun to create a new file.
  *
  * If this method is not overridden it will function under the
  * assumption that the [writer] function calls the [printer] function
  * and writes it output to a location specified in the [KlutterWriter] class.
  *
  * @return logger with info about the steps done by the generator.
  */
 open fun generate() = writer().write()

 /**
  * @return a [KlutterPrinter] to create the content of a file.
  */
 abstract fun printer(): KlutterPrinter

 /**
  * @return a [KlutterWriter] to write the content to a file location.
  */
 abstract fun writer(): KlutterWriter

}

/**
 * Utility interface which takes (generated) file content and writes it to a file.
 * Used for editing and/or creating classes and configuration files.
 *
 * KlutterWriter is an class to be used by [KlutterFileGenerator] and
 * should not be exposed for external usage.
 */
interface KlutterWriter {

 /**
  * Creates a new file and writes the content to said file.
  * @return logger with info about the steps done by the writer.
  */
 fun write(): KlutterLogger

}

/**
 * The purpose of this class is to output the content of a file.
 *
 * KlutterPrinter is an class to be used by [KlutterFileGenerator] and
 * should not be exposed for external usage.
 */
interface KlutterPrinter {

 /**
  * @return the created file content as String.
  */
 fun print(): String

}

/**
 * The purpose of a KlutterProducer is to produce a set of files.
 */
interface KlutterProducer {

 /**
  * Creates one or more files.
  * @return logger with info about the steps done by the producer.
  */
 fun produce(): KlutterLogger

}

/**
 * Utility interface which processes a given file and may or may not change it's content.
 */
interface KlutterVisitor {

 /**
  * Reads the content of a file and determines if any editting should be done.
  * @return logger with info about the steps done by the visitor.
  */
 fun visit(): KlutterLogger

}