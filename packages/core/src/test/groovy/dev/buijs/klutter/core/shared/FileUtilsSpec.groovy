package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.CoreTestUtil
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

}
