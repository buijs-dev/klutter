package dev.buijs.klutter.core.shared


import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.project.PubspecKt
import dev.buijs.klutter.core.project.Root
import dev.buijs.klutter.core.test.TestResource
import spock.lang.Specification

import java.nio.file.Files

class FileUtilsSpec extends Specification {

    def static resources = new TestResource()

    def "File.toPubspecData should return correct package and plugin name"(){

        given:
        def yaml = Files.createTempFile("","pubspec.yaml").toFile()

        and:
        resources.copy("plugin_pubspec", yaml)

        when:
        def dto = PubspecKt.toPubspec(yaml)

        then:
        dto.name == "super_awesome"
        dto.android.pluginPackage$core == "foo.bar.super_awesome"
        dto.android.pluginClass$core == "SuperAwesomePlugin"
        dto.ios.pluginClass$core == "SuperAwesomePlugin"

    }

    def "Root.toPubspecData should return correct package and plugin name"(){

        given:
        def folder = Files.createTempDirectory("").toFile()
        def root = new Root("super_awesome", folder)
        def yaml = new File("${folder.path}/pubspec.yaml")

        and:
        resources.copy("plugin_pubspec", yaml)

        when:
        def dto = PubspecKt.toPubspec(root)

        then:
        dto.name == "super_awesome"
        dto.android.pluginPackage$core == "foo.bar.super_awesome"
        dto.android.pluginClass$core == "SuperAwesomePlugin"
        dto.ios.pluginClass$core == "SuperAwesomePlugin"

    }

    def "An exception is thrown if the file does not exist"() {

        when:
        FileUtilsKt.verifyExists(new File("/fake"))

        then:
        KlutterException e = thrown()
        e.getMessage() == "Path does not exist: /fake"

    }

    def "The file is returned if it exists"() {

        when:
        def file = FileUtilsKt.verifyExists(new File("/"))

        then:
        file.exists()

    }

    def "If a file exists then it is not created"() {

        given:
        def folder = Files.createTempDirectory("yxz")
        def file = folder.resolve("foo.txt").toFile()

        and: "file is created"
        file.createNewFile()

        and: "file exists"
        file.exists()

        when:
        FileUtilsKt.maybeCreate(file)

        then:
        file.exists()

    }

    def "If a file does not exists then it is created"() {

        given:
        def folder = Files.createTempDirectory("abc")
        def file = folder.resolve("foo.txt").toFile()

        and: "file does not exist"
        !file.exists()

        when:
        FileUtilsKt.maybeCreate(file)

        then: "file does exist"
        file.exists()

    }

    def "If a file does not exists after creating it an exception is thrown"() {

        given: "a mocked file that never will exist"
        def file = GroovyMock(File) {
            it.exists() >> false
            it.createNewFile() >> true
        }

        when:
        FileUtilsKt.maybeCreate(file)

        then:
        KlutterException e = thrown()
        e.getMessage() == "Failed to create file: Mock for type 'File' named 'file'"

    }

    def "If file exists then KlutterWriter overwrites it"() {

        given:
        def folder = Files.createTempDirectory("hij")
        def file = folder.resolve("foo.txt").toFile()

        and: "file is created"
        file.createNewFile()

        and: "file exists"
        file.exists()

        and: "file has content"
        file.write(":-(")

        when:
        new FileWriter(file, ";-)").write()

        then: "file is overwritten"
        file.text == ";-)"

    }

    def "If file does not exists then KlutterWriter creates it"() {

        given:
        def folder = Files.createTempDirectory("klm")
        def file = folder.resolve("foo.txt").toFile()

        and: "file does not exist"
        !file.exists()

        when:
        new FileWriter(file, ";-)").write()

        then: "file is created"
        file.text == ";-)"

    }

    def "When excludeArm64 regex can not determine prefix value it defaults to 's' "(){

        given:
        def file = Files.createTempFile("","pubspec.yaml").toFile()

        and: "the line excludeArm64 will look for"
        file.write("s.dependency'Flutter'")

        when:
        FileUtilsKt.excludeArm64(file, "dependency'Flutter'")

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
        FileUtilsKt.excludeArm64(file, "dependency'Flutter'")

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
        FileUtilsKt.excludeArm64(file, "ios.deployment_target")

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
