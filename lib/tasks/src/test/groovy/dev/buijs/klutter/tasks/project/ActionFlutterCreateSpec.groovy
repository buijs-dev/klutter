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
package dev.buijs.klutter.tasks.project

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import dev.buijs.klutter.kore.project.Architecture
import dev.buijs.klutter.kore.project.FlutterDistribution
import dev.buijs.klutter.kore.project.OperatingSystem
import dev.buijs.klutter.kore.project.Version
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class ActionFlutterCreateSpec extends Specification {

    @Shared
    EitherOk validRootFolder

    @Shared
    EitherNok invalidRootFolder

    @Shared
    EitherOk validPluginName

    @Shared
    EitherNok invalidPluginName

    @Shared
    EitherOk validGroupName

    @Shared
    EitherNok invalidGroupName

    @Shared
    EitherNok invalidFlutterPath

    @Shared
    FlutterDistribution flutterDistribution

    def setupSpec() {
        def root = Files.createTempDirectory("").toFile()
        validRootFolder = Either.ok(root)
        invalidRootFolder = Either.nok("Root does NOT exist")
        validPluginName = Either.ok("my_plugin")
        invalidPluginName = Either.nok("Not a valid plugin name")
        validGroupName = Either.ok("com.example")
        invalidGroupName = Either.nok("Not a valid group name")
        invalidFlutterPath = Either.nok("Folder does not exist")
        flutterDistribution = new FlutterDistribution(new Version(3,0,5), OperatingSystem.MACOS, Architecture.ARM64)
    }

    def "When RootFolder is invalid then a KlutterException is thrown"() {
        given:
        def sut = new RunFlutterCreate(
                validPluginName,
                validGroupName,
                invalidRootFolder,
                flutterDistribution)

        when:
        sut.doAction()

        then:
        KlutterException e = thrown()
        e.message == "Root does NOT exist"
    }

    def "When PluginName is invalid then a KlutterException is thrown"() {
        given:
        def sut = new RunFlutterCreate(
                invalidPluginName,
                validGroupName,
                validRootFolder,
                flutterDistribution)

        when:
        sut.doAction()

        then:
        KlutterException e = thrown()
        e.message == "Not a valid plugin name"
    }

    def "When GroupName is invalid then a KlutterException is thrown"() {
        given:
        def sut = new RunFlutterCreate(
                validPluginName,
                invalidGroupName,
                validRootFolder,
                flutterDistribution)

        when:
        sut.doAction()

        then:
        KlutterException e = thrown()
        e.message == "Not a valid group name"
    }

}