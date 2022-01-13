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


package dev.buijs.klutter.plugins.gradle.tasks.config


import dev.buijs.klutter.core.*
import java.nio.file.Path

class KlutterPropertiesGenerator(
    private val path: Path,
    private val properties: List<YamlProperty>,
): KlutterFileGenerator() {

    override fun printer() = KlutterPropertiesPrinter(properties)

    override fun writer() = KlutterPropertiesWriter(path, printer().print())

}

class KlutterPropertiesPrinter(
    private val properties: List<YamlProperty>)
    : KlutterPrinter
{

    override fun print(): String {
        val sb = StringBuilder()
        properties.forEach { property ->
            val key = property.key
            val value = property.value
            sb.append("$key=$value\r\n")
        }
        return sb.toString()
    }

}


class KlutterPropertiesWriter(
    val path: Path,
    val content: String)
    : KlutterWriter
{

    override fun write(): KlutterLogger {
        val logger = KlutterLogger()

        val file = path.resolve("klutter.properties").toAbsolutePath().toFile()

        if(file.exists()){
            logger.info("Deleting existing file: $file")
            file.delete()
        }

        file.createNewFile()
        logger.info("Created new file: $file")
        file.writeText(content)
        return logger
    }

}