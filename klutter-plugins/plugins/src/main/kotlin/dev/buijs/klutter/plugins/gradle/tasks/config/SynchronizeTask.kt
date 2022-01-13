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
import dev.buijs.klutter.core.YamlProperty
import dev.buijs.klutter.core.YamlReader
import dev.buijs.klutter.plugins.gradle.KlutterTask
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.io.File
import javax.inject.Inject

/**
 * @author Gillian Buijs
 */
open class SynchronizeTask
@Inject constructor(styledTextOutputFactory: StyledTextOutputFactory):
    KlutterTask(styledTextOutputFactory)
{
    /**
     * Write config to klutter module and every module defined in the Klutter Gradle Plugin
     */
    override fun describe() {
        val properties = getProperties()
        val modules = mutableListOf(project.rootDir)
        modules.addAll(modules()?: emptyList())
        modules.forEach { module ->
            if(module.exists()) {
                val producer = KlutterConfigProducer(module.toPath(), properties)
                producer.produce()
                logger.merge(producer.logger)
            } else logger.error("Module directory does not exist: ${module.absolutePath}")
        }
    }

    private fun getProperties(): List<YamlProperty> {
        val configYaml = getFile("klutter/klutter.yaml", failWhenNotExists = true)!!
        val localYaml  = getFile("klutter/klutter-local.yaml", failWhenNotExists = false)
        val secretYaml = getFile("klutter/klutter-secrets.yaml", failWhenNotExists = false)

        return mutableListOf<YamlProperty>().also {
            YamlReader().read(configYaml).also {
                properties -> it.addAll(properties)
            }

            secretYaml?.let { yaml -> YamlReader().read(yaml) }.also { maybeProperties ->
                maybeProperties?.let { properties -> it.addAll(properties) }
            }

            localYaml?.let { yaml -> YamlReader().read(yaml) }.also { maybeProperties ->
                maybeProperties?.let { properties -> it.addAll(properties) }
            }
        }

    }

    private fun getFile(filename: String, failWhenNotExists: Boolean = true): File? {
        val file = project.rootDir.resolve(filename).absoluteFile
        return if (!file.exists()) {
            if(failWhenNotExists) {
                throw KlutterConfigException("File location for '$name' does not exist: '${file.absolutePath}'")
            } else null
        } else file
    }

}