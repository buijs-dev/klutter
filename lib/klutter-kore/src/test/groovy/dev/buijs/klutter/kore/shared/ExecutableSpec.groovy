package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.KlutterException
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class ExecutableSpec extends Specification {

    @Shared
    def working = Files.createTempDirectory("").toFile()

    def "Verify executing a command"() {

        given:
        def sayHello = ExecutableKt.execute('''echo hello world''', working)

        expect:
        sayHello == "hello world\n"

    }

    def "When a command fails an exception is thrown"() {

        when:
        ExecutableKt.execute('''mv foo''', working)

        then:
        KlutterException e = thrown()
        e.message == "Failed to execute command: \nusage: " +
                "mv [-f | -i | -n] [-hv] source target\n" +
                "       mv [-f | -i | -n] [-v] source ... directory\n"

    }

}