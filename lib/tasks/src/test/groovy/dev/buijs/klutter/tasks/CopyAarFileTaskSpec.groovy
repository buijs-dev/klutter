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
package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterException
import spock.lang.Specification
import java.nio.file.Files

class CopyAarFileTaskSpec extends Specification {

    def "For plugin copies platform-release.aar from platform to root/android/klutter folder"() {
        given:
        def root = Files.createTempDirectory("")
                .toAbsolutePath()
                .toFile()

        def target = new File("${root.absolutePath}/android/klutter")
        target.mkdirs()

        def buildFolder = new File("${root.absolutePath}/platform/build/outputs/aar")
        buildFolder.mkdirs()

        def aarFile = new File("${buildFolder.absolutePath}/platform-release.aar")
        aarFile.createNewFile()
        aarFile.write("TC2")

        when:
        new CopyAarFileTask(root, null).run()

        then:
        with(target.absolutePath) {
            new File("${it}/platform.aar").exists()
            new File("${it}/platform.aar").text == "TC2"
        }
    }

    def "For plugin copies plugin_name-release.aar from platform to root/android/klutter folder"() {
        given:
        def pluginName = "my_plugin"
        def root = Files.createTempDirectory("")
                .toAbsolutePath()
                .toFile()

        def target = new File("${root.absolutePath}/android/klutter")
        target.mkdirs()

        def buildFolder = new File("${root.absolutePath}/platform/build/outputs/aar")
        buildFolder.mkdirs()

        def aarFile = new File("${buildFolder.absolutePath}/$pluginName-release.aar")
        aarFile.createNewFile()
        aarFile.write("TC2")

        when:
        new CopyAarFileTask(root, pluginName).run()

        then:
        with(target.absolutePath) {
            new File("${it}/platform.aar").exists()
            new File("${it}/platform.aar").text == "TC2"
        }
    }

    def "An exception is thrown when the aar file does not exist"() {
        given:
        def pluginName = "my_plugin"
        def root = Files.createTempDirectory("")
                .toAbsolutePath()
                .toFile()

        def target = new File("${root.absolutePath}/android/klutter")
        target.mkdirs()

        when:
        new CopyAarFileTask(root, pluginName).run()

        then:
        KlutterException e = thrown()
        e.message.startsWith("Path does not exist:")
        e.message.endsWith("my_plugin-release.aar")
    }

    def "An exception is thrown when the root/android/klutter folder does not exist"() {
        given:
        def pluginName = "my_plugin"
        def root = Files.createTempDirectory("")
                .toAbsolutePath()
                .toFile()

        def buildFolder = new File("${root.absolutePath}/platform/build/outputs/aar")
        buildFolder.mkdirs()

        def aarFile = new File("${buildFolder.absolutePath}/$pluginName-release.aar")
        aarFile.createNewFile()
        aarFile.write("TC4")

        when:
        new CopyAarFileTask(root, pluginName).run()

        then:
        KlutterException e = thrown()
        e.message.startsWith("Path does not exist:")
        e.message.endsWith("/android/klutter")
    }

}