package dev.buijs.klutter.core

import spock.lang.Specification

class FileUtilsKtSpec extends Specification {

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
}
