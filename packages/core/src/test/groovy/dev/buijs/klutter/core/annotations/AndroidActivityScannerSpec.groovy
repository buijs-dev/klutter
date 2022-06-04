package dev.buijs.klutter.core.annotations

import dev.buijs.klutter.core.KlutterException
import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class AndroidActivityScannerSpec extends Specification {

    def "If the folder does not exist then a KlutterException is thrown"(){
        given:
        def folder = new File("/FAKE")

        expect:
        exception(folder, "Path does not exist: /FAKE")
    }

    def "If folder does not contain a MainActivity file then a KlutterException is thrown"(){
        given:
        def folder = Files.createTempDirectory("").toFile()

        expect:
        exception(folder, "MainActivity not found or the @KlutterAdapter is missing in folder $folder.absolutePath.")
    }

    def "If folder does contain a MainActivity but not an KlutterAdapter annotation then a KlutterException is thrown"(){
        given:
        def folder = Files.createTempDirectory("").toFile()
        new File("$folder.absolutePath/MainActivity.kt").createNewFile()

        expect:
        exception(folder, "MainActivity not found or the @KlutterAdapter is missing in folder $folder.absolutePath.")
    }

    def "If folder contains multiple MainActivity with KlutterAdapter annotation then a KlutterException is thrown"(){
        given:
        def folder = Files.createTempDirectory("").toFile()
        new File("$folder.absolutePath/foo").mkdirs()

        and:
        def activity1 = new File("$folder.absolutePath/MainActivity.kt")
        activity1.createNewFile()
        activity1.write("@KlutterAdapter")

        def activity2 = new File("$folder.absolutePath/foo/MainActivity.kt")
        activity2.createNewFile()
        activity2.write("@KlutterAdapter")

        expect:
        exception(folder, "Expected to find one @KlutterAdapter annotation in the MainActivity file but found 2 files.")
    }

    def "If folder contains 1 MainActivity with KlutterAdapter annotation then that file is returned"(){
        given:
        def folder = Files.createTempDirectory("").toFile()
        new File("$folder.absolutePath/foo").mkdirs()

        and:
        def activity1 = new File("$folder.absolutePath/MainActivity.kt")
        activity1.createNewFile()
        activity1.write("@KlutterAdapter Hooray!")

        when:
        def file = new AndroidActivityScanner(folder).scan$core()

        then:
        file.text == "@KlutterAdapter Hooray!"
    }

    private static exception(File folder, String message) {
        try {
            new AndroidActivityScanner(folder).scan$core()
        } catch (KlutterException e) {
            assert e.message == message
        }
        true
    }

}
