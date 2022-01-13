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

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.KlutterProducer
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.YamlProperty
import java.nio.file.Path
import kotlin.io.path.exists


/**
 * @author Gillian Buijs
 */
@Suppress("unused")
class KlutterConfigProducer(
    private val module: Path,
    private val properties: List<YamlProperty>): KlutterProducer {

    val logger = KlutterLogger()

    override fun produce(): KlutterLogger {
        val directory = createKlutterDirectory(module)
        val gradleGenerator = KlutterGradleFileGenerator(directory, properties)
        val propertiesGenerator = KlutterPropertiesGenerator(directory, properties)
        return logger
            .merge(gradleGenerator.generate())
            .merge(propertiesGenerator.generate())
    }

    private fun createKlutterDirectory(root: Path): Path {
        if(!root.exists()){
            throw KlutterConfigException("Path to module directory does not exist: $root")
        }

        val directory = root.resolve(".klutter").toAbsolutePath().toFile()
        directory.mkdirs()
        logger.info("Created directory: $directory")
        return directory.toPath()
    }

}