/* Copyright (c) 2021 - 2023 Buijs Software
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
package dev.buijs.klutter.kommand

import dev.buijs.klutter.kommand.flutterw.DownloaderKt
import dev.buijs.klutter.kommand.project.ByCommandKt
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class ByCommandSpec extends Specification {

    @Shared
    File rootFolder = Files.createTempDirectory("").toFile()

    @Shared
    File flutterSDK = rootFolder.toPath().resolve("flutter").toFile()

    @Shared
    File configYaml = rootFolder.toPath().resolve("klutter.yaml").toFile()

    @Shared
    String overrideBomVersion = "9999.1.1.zeta"

    def setupSpec() {
        DownloaderKt.dryRun = true
        flutterSDK.mkdir()
        configYaml.createNewFile()
        configYaml.write("""
bom-version: $overrideBomVersion
""")
    }

    def "Verify specifying only required args returns ProjectBuilderOptions"() {
        given:
        def command = [
                "--root", rootFolder.path,
                "--group", "com.example",
                "--name", "my_awesome_plugin",
                "--flutter", "3.0.5.X64",
        ]

        when:
        def options = ByCommandKt.projectBuilderOptionsByCommand(command)

        then:
        options.pluginName.data == "my_awesome_plugin"
        options.groupName.data == "com.example"
        options.rootFolder.data == rootFolder
        options.config == null
    }

    def "When config is specified and the File exists then ProjectBuilderOptions contains config"() {
        given:
        def command = [
                "--root", rootFolder.path, "" +
                "--group", "com.example",
                "--name", "my_awesome_plugin",
                "--flutter", "3.0.5.X64",
                "--config", configYaml.absolutePath
        ]

        when:
        def options = ByCommandKt.projectBuilderOptionsByCommand(command)

        then:
        options.pluginName.data == "my_awesome_plugin"
        options.groupName.data == "com.example"
        options.rootFolder.data == rootFolder
        options.config != null
        options.config.bomVersion == overrideBomVersion
    }

}
