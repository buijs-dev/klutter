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
package dev.buijs.klutter.compiler.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import dev.buijs.klutter.kore.KlutterException
import kotlin.KotlinVersion
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class ProcessorProviderSpec extends Specification {

    @Shared
    File scanFolder = Files.createTempDirectory("").toFile()

    @Shared
    File outputFolder = Files.createTempDirectory("").toFile()

    @Shared
    File flutterSDKFolder = Files.createTempDirectory("flutterbin").toFile()

    def setupSpec() {
        ProcessorOptionsKt.dryRun = true
    }

    def "Verify that ProcessorProvider parses options from KSP properly" () {
        given:
        def options = [
                "klutterScanFolder": scanFolder.path,
                "klutterOutputFolder": outputFolder.path,
                "klutterGenerateAdapters": "false",
                "intelMac": "false",
                "flutterVersion": "3.0.5.windows.x64"
        ]

        and:
        def env = new SymbolProcessorEnvironment(options, new KotlinVersion(1,8,20), Stub(CodeGenerator), Stub(KSPLogger))

        and:
        def provider = new ProcessorProvider()

        when:
        def processor = provider.create(env)

        then:
        processor != null
    }

    def "A KlutterException is thrown if klutterScanFolder is NOT set" () {
        given:
        def options = [
                "klutterScanFolder": null,
                "klutterOutputFolder": outputFolder.path,
                "klutterGenerateAdapters": "false",
                "intelMac": "false",
                "flutterSDKPath": flutterSDKFolder.path
        ]

        and:
        def env = new SymbolProcessorEnvironment(options, new KotlinVersion(1,8,20), Stub(CodeGenerator), Stub(KSPLogger))

        and:
        def provider = new ProcessorProvider()

        when:
        provider.create(env)

        then:
        KlutterException e = thrown()
        e.message.contains("Option klutterScanFolder not set!")
    }

    def "A KlutterException is thrown if klutterOutputFolder is NOT set" () {
        given:
        def options = [
                "klutterScanFolder": scanFolder.path,
                "klutterOutputFolder": null,
                "klutterGenerateAdapters": "false",
                "intelMac": "false",
                "flutterSDKPath": flutterSDKFolder.path
        ]

        and:
        def env = new SymbolProcessorEnvironment(options, new KotlinVersion(1,8,20), Stub(CodeGenerator), Stub(KSPLogger))

        and:
        def provider = new ProcessorProvider()

        when:
        provider.create(env)

        then:
        KlutterException e = thrown()
        e.message.contains("Option klutterOutputFolder not set!")
    }

    def "A KlutterException is thrown if flutterSDKPath is NOT set" () {
        given:
        def options = [
                "klutterScanFolder": scanFolder.path,
                "klutterOutputFolder": outputFolder.path,
                "klutterGenerateAdapters": "false",
                "intelMac": "false"
        ]

        and:
        def env = new SymbolProcessorEnvironment(options, new KotlinVersion(1,8,20), Stub(CodeGenerator), Stub(KSPLogger))

        and:
        def provider = new ProcessorProvider()

        when:
        provider.create(env)

        then:
        KlutterException e = thrown()
        e.message.contains(" arg(\"flutterVersion\", <Flutter Version in format major.minor.patch, example: 3.0.5)")
    }
}