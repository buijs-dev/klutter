package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterException
import spock.lang.Specification

import java.nio.file.Files

class ExcludeArm64Spec extends Specification {

    def "When excludeArm64 regex can not determine prefix value it defaults to 's' "(){

        given:
        def file = Files.createTempFile("","pubspec.yaml").toFile()

        and: "the line excludeArm64 will look for"
        file.write("s.dependency'Flutter'")

        when:
        GenerateAdaptersForPluginTaskKt.excludeArm64(file, "dependency'Flutter'")

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
        GenerateAdaptersForPluginTaskKt.excludeArm64(file, "dependency'Flutter'")

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
        GenerateAdaptersForPluginTaskKt.excludeArm64(file, "ios.deployment_target")

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
