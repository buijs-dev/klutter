package dev.buijs.klutter.core

import spock.lang.Specification

import java.nio.file.Files

class UtilsSpec extends Specification {

    def "An exception is thrown if the file does not exist"() {

        when:
        UtilsKt.verifyExists(new File("/fake"))

        then:
        KlutterException e = thrown()
        e.getMessage() == "Path does not exist: /fake"

    }

    def "The file is returned if it exists"() {

        when:
        def file = UtilsKt.verifyExists(new File("/"))

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
        UtilsKt.maybeCreate(file)

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
        UtilsKt.maybeCreate(file)

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
        UtilsKt.maybeCreate(file)

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
}
