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
package dev.buijs.klutter.kore.tasks.codegen

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.tasks.codegen.ExcludeArm64Task
import spock.lang.Specification

import java.nio.file.Files

class ExcludeArm64TaskSpec extends Specification {

    def "When excludeArm64 regex can not determine prefix value it defaults to 's' "(){

        given:
        def file = Files.createTempFile("","pubspec.yaml").toFile()

        and: "the line excludeArm64 will look for"
        file.write("s.dependency'Flutter'")

        when:
        new ExcludeArm64Task(file, "dependency'Flutter'", true).run()

        then:
        with(file.text) {
            it.contains("""s.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }""")
            it.contains("""s.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }""")
        }

    }

    def "When excludeArm64 fails to add exclusions then an exception is thrown "(){

        given:
        def file = Files.createTempFile("","pubspec.yaml").toFile()

        and:
        file.write(podfile)

        when:
        new ExcludeArm64Task(file, "dependency'Flutter'", true).run()

        then:
        KlutterException e = thrown()
        e.message.startsWith("Failed to add exclusions for arm64.")

        where:
        podfile << [
                "",
                """s.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }""",
                """s.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }"""
        ]

    }

    def "Verify excluded lines are not duplicated"(){

        given:
        def file = Files.createTempFile("","Podfile").toFile()

        and:
        file.write(podfile)

        when:
        new ExcludeArm64Task(file, "ios.deployment_target", true).run()

        then:
        file.text.replaceAll(" ", "") == expected.replaceAll(" ", "")

        where:
        podfile << [
                """
                    Pod::Spec.new do |spec|
                    
                    spec.ios.deployment_target = '14.1'
                    spec.pod_target_xcconfig = {
                        'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                        'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
                    }       
                """,
                """
                    Pod::Spec.new do |spec|
                    
                    spec.ios.deployment_target = '14.1'
                    spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
                    spec.pod_target_xcconfig = {
                        'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                        'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
                    }       
                """,
                """
                    Pod::Spec.new do |spec|
                    
                    spec.ios.deployment_target = '14.1'
                    spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
                    spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
                    spec.pod_target_xcconfig = {
                        'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                        'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
                    }       
                """,
                """
                    Pod::Spec.new do |spec|
                    
                    spec.ios.deployment_target = '14.1'
                    spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
                    spec.pod_target_xcconfig = {
                        'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                        'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
                    }       
                """,
        ]

        expected << [
                """
            Pod::Spec.new do |spec|
            
            spec.ios.deployment_target = '14.1'
            spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.pod_target_xcconfig = {
                'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
            }       
        """,
                """
            Pod::Spec.new do |spec|
            
            spec.ios.deployment_target = '14.1'
            spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.pod_target_xcconfig = {
                'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
            }       
        """,
                """
            Pod::Spec.new do |spec|
            
            spec.ios.deployment_target = '14.1'
            spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.pod_target_xcconfig = {
                'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
            }       
        """,
                // pod and user line are reversed, all other are identical.
                // this is because pod_target was already in the file.
                // could be fixed by implementing a proper parser instead of
                // processing line by line.
                """
            Pod::Spec.new do |spec|
            
            spec.ios.deployment_target = '14.1'
            spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.pod_target_xcconfig = {
                'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
            }       
        """,
        ]
    }


}
