package dev.buijs.klutter.kore.project

import dev.buijs.klutter.kore.KlutterException
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
        pubspec.flutter == null
    }

    def "If pubspec contains flutter block without plugin then it is null" () {
        given:
        def yaml = Files.createTempFile("pubspec", ".yaml").toFile()
        yaml.write("""
            name: ridiculous_plugin
            flutter:
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        pubspec.flutter == null
        pubspec.plugin == null
        pubspec.platforms == null
        pubspec.ios == null
        pubspec.android == null
    }

    def "If pubspec contains flutter plugin block without content then it is not null" () {
        given:
        def yaml = Files.createTempFile("pubspec", ".yaml").toFile()
        yaml.write("""
            name: ridiculous_plugin
            flutter:
              plugin:
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        pubspec.flutter != null
        pubspec.plugin == null
    }

    def "Platforms without ios returns null for ios getter" () {
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
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        pubspec.platforms != null
        pubspec.ios == null
    }

    def "IOS without pluginclass returns null" () {
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
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        pubspec.ios == null
    }

    def "Verify parsing complete pubspec.yaml" () {
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
        pubspec.flutter != null
        pubspec.flutter.plugin != null
        pubspec.plugin != null
        pubspec.platforms != null
        pubspec.android != null
        pubspec.android.pluginPackage == "some.company.ridiculous_plugin"
        pubspec.android.pluginClass == "RidiculousPlugin"
        pubspec.ios != null
        pubspec.ios.pluginClass == "RidiculousPlugin"

    }

    def "iosClassName returns orElse value if ios is null" () {
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
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        PubspecKt.iosClassName(pubspec, "SomeClass") == "SomeClass"

    }

    def "iosClassName returns orElse value if ios pluginClass is null" () {
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
                    pluginClass:
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        PubspecKt.iosClassName(pubspec, "SomeClass") == "SomeClass"

    }

    def "iosClassName returns value from YAML if present" () {
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
        PubspecKt.iosClassName(pubspec, "SomeClass") == "RidiculousPlugin"

    }

    def "androidClassName returns orElse value if ios is null" () {
        given:
        def yaml = Files.createTempFile("pubspec", ".yaml").toFile()
        yaml.write("""
            name: ridiculous_plugin
            flutter:
              plugin:
                platforms:
                  android:
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        PubspecKt.androidClassName(pubspec, "SomeClass") == "SomeClass"

    }

    def "androidClassName returns orElse value if ios pluginClass is null" () {
        given:
        def yaml = Files.createTempFile("pubspec", ".yaml").toFile()
        yaml.write("""
            name: ridiculous_plugin
            flutter:
              plugin:
                platforms:
                  android:
                    package: some.company.ridiculous_plugin
                    pluginClass:
                
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        PubspecKt.androidClassName(pubspec, "SomeClass") == "SomeClass"

    }

    def "androidClassName returns value from YAML if present" () {
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
        PubspecKt.androidClassName(pubspec, "SomeClass") == "RidiculousPlugin"

    }

    def "androidPackageName returns orElse value if ios is null" () {
        given:
        def yaml = Files.createTempFile("pubspec", ".yaml").toFile()
        yaml.write("""
            name: ridiculous_plugin
            flutter:
              plugin:
                platforms:
                  android:
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        PubspecKt.androidPackageName(pubspec) == ""

    }

    def "androidPackageName returns orElse value if ios pluginClass is null" () {
        given:
        def yaml = Files.createTempFile("pubspec", ".yaml").toFile()
        yaml.write("""
            name: ridiculous_plugin
            flutter:
              plugin:
                platforms:
                  android:
                    package:
                    pluginClass:
                
        """)

        when:
        def pubspec = PubspecKt.toPubspec(yaml)

        then:
        PubspecKt.androidPackageName(pubspec) == ""

    }

    def "androidPackageName returns value from YAML if present" () {
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
        PubspecKt.androidPackageName(pubspec) == "some.company.ridiculous_plugin"

    }
}