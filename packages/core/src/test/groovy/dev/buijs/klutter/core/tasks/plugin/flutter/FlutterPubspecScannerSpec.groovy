package dev.buijs.klutter.core.tasks.plugin.flutter

import dev.buijs.klutter.core.test.TestResource
import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class FlutterPubspecScannerSpec extends Specification {

    def static resources = new TestResource()

    def "FlutterPubspecScanner should return correct package and plugin name"(){

        given:
        def yaml = Files.createTempFile("","pubspec.yaml").toFile()

        and:
        resources.copy("plugin_pubspec", yaml)

        when:
        def dto = new FlutterPubspecScanner(yaml).scan()

        then:
        dto.libraryName == "super_awesome"
        dto.packageName == "foo.bar.super_awesome"
        dto.pluginClassName == "SuperAwesomePlugin"

    }

}
