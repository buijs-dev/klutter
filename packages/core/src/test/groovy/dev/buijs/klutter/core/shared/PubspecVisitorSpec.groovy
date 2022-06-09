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

package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.Root
import dev.buijs.klutter.core.test.TestResource
import spock.lang.Specification
import java.nio.file.Files

class PubspecVisitorSpec extends Specification {

    def static resources = new TestResource()

    def "File.toPubspecData should return correct package and plugin name"(){

        given:
        def yaml = Files.createTempFile("","pubspec.yaml").toFile()

        and:
        resources.copy("plugin_pubspec", yaml)

        when:
        def dto = PubspecVisitorKt.toPubspecData(yaml)

        then:
        dto.name == "super_awesome"
        dto.android.pluginPackage$core == "foo.bar.super_awesome"
        dto.android.pluginClass$core == "SuperAwesomePlugin"
        dto.ios.pluginClass$core == "SuperAwesomePlugin"

    }

    def "Root.toPubspecData should return correct package and plugin name"(){

        given:
        def folder = Files.createTempDirectory("").toFile()
        def root = new Root(folder)
        def yaml = new File("${folder.path}/pubspec.yaml")

        and:
        resources.copy("plugin_pubspec", yaml)

        when:
        def dto = PubspecVisitorKt.toPubspecData(root)

        then:
        dto.name == "super_awesome"
        dto.android.pluginPackage$core == "foo.bar.super_awesome"
        dto.android.pluginClass$core == "SuperAwesomePlugin"
        dto.ios.pluginClass$core == "SuperAwesomePlugin"

    }

}
