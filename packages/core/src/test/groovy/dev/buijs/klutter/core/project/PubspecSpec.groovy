package dev.buijs.klutter.core.project

import dev.buijs.klutter.core.KlutterException
import spock.lang.Specification

import java.nio.file.Files

class PubspecSpec extends Specification {

    def "If pubspec parsing fails an exception is thrown" () {
        given:
        def pubspec = GroovyMock(File) {
            it.exists() >> true
        }

        when:
        PubspecKt.toPubspec(pubspec)

        then:
        KlutterException e = thrown()
        e.message == "Failed to parse pubspec.yaml: null"
    }

    def "If pubspec contains no flutter block then it is null" () {
        given:
        def yaml = Files.createTempFile("pubspec", ".yaml").toFile()
        yaml.write("""
            name: ridiculous_plugin
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        pubspec.flutter$core == null
    }

    def "Verify parsing fully pubspec.yaml" () {
        given:
        def yaml = Files.createTempFile("pubspec", ".yaml").toFile()
        yaml.write("""
            name: ridiculous_plugin
            flutter:
              plugin:
                platforms:
                  android:
                    package: some.company.ridiculous_plugin
                    pluginClass: RidiculousPlugin
                  ios:
                    pluginClass: RidiculousPlugin
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        pubspec.flutter$core != null
        pubspec.flutter$core.plugin$core != null
        pubspec.plugin != null
        pubspec.platforms != null
        pubspec.android != null
        pubspec.android.pluginPackage$core == "some.company.ridiculous_plugin"
        pubspec.android.pluginClass$core == "RidiculousPlugin"
        pubspec.ios != null
        pubspec.ios.pluginClass$core == "RidiculousPlugin"

    }

}