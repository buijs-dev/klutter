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
        PubspecBuilder.toPubspec(pubspec)

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.iosClassName(pubspec, "SomeClass") == "SomeClass"

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.iosClassName(pubspec, "SomeClass") == "SomeClass"

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.iosClassName(pubspec, "SomeClass") == "RidiculousPlugin"

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.androidClassName(pubspec, "SomeClass") == "SomeClass"

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.androidClassName(pubspec, "SomeClass") == "SomeClass"

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.androidClassName(pubspec, "SomeClass") == "RidiculousPlugin"

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.androidPackageName(pubspec) == ""

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.androidPackageName(pubspec) == ""

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
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.androidPackageName(pubspec) == "some.company.ridiculous_plugin"

    }


    def "verify all unknown properties are ignored" () {
        given:
        def yaml = Files.createTempFile("pubspec", ".yaml").toFile()
        yaml.write(pubspecContent)

        when:
        def pubspec = PubspecBuilder.toPubspec(yaml)

        then:
        PubspecBuilder.androidPackageName(pubspec) == "some.company.ridiculous_plugin"

        where:
        pubspecContent = """
            name: my_plugin
            description: A new Flutter project.
            version: 0.0.1
            homepage:
            
            environment:
              sdk: ">=2.18.0 <4.0.0"
              flutter: ">=2.5.0"
            
            dependencies:
              flutter:
                sdk: flutter
              klutter: ^0.2.3
              squint_json: ^0.1.0
            
            dev_dependencies:
              flutter_test:
                sdk: flutter
              flutter_lints: ^2.0.0
            
            # For information on the generic Dart part of this file, see the
            # following page: https://dart.dev/tools/pub/pubspec
            
            # The following section is specific to Flutter packages.
            flutter:
              # This section identifies this Flutter project as a plugin project.
              # The 'pluginClass' specifies the class (in Java, Kotlin, Swift, Objective-C, etc.)
              # which should be registered in the plugin registry. This is required for
              # using method channels.
              # The Android 'package' specifies package in which the registered class is.
              # This is required for using method channels on Android.
              # The 'ffiPlugin' specifies that native code should be built and bundled.
              # This is required for using `dart:ffi`.
              # All these are used by the tooling to maintain consistency when
              # adding or updating assets for this project.
              plugin:
                platforms:
                  android:
                    package: some.company.ridiculous_plugin
                    pluginClass: MyPlugin2Plugin
                  ios:
                    pluginClass: MyPlugin2Plugin
                  web:
                    pluginClass: MyPlugin2Web
                    fileName: my_plugin2_web.dart
            
              # To add assets to your plugin package, add an assets section, like this:
              # assets:
              #   - images/a_dot_burr.jpeg
              #   - images/a_dot_ham.jpeg
              #
              # For details regarding assets in packages, see
              # https://flutter.dev/assets-and-images/#from-packages
              #
              # An image asset can refer to one or more resolution-specific "variants", see
              # https://flutter.dev/assets-and-images/#resolution-aware
            
              # To add custom fonts to your plugin package, add a fonts section here,
              # in this "flutter" section. Each entry in this list should have a
              # "family" key with the font family name, and a "fonts" key with a
              # list giving the asset and other descriptors for the font. For
              # example:
              # fonts:
              #   - family: Schyler
              #     fonts:
              #       - asset: fonts/Schyler-Regular.ttf
              #       - asset: fonts/Schyler-Italic.ttf
              #         style: italic
              #   - family: Trajan Pro
              #     fonts:
              #       - asset: fonts/TrajanPro.ttf
              #       - asset: fonts/TrajanPro_Bold.ttf
              #         weight: 700
              #
              # For details regarding fonts in packages, see
              # https://flutter.dev/custom-fonts/#from-packages
            """
    }

}