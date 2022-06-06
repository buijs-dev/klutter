package dev.buijs.klutter.core

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

import static dev.buijs.klutter.core.test.KlutterTest.*

class ProjectSpec extends Specification {

    @Shared
    def plugin = plugin { project, resources -> }

    def "Verify creating a dev.buijs.klutter.core.Project based of a String location"() {
        expect:
        with(ProjectKt.klutterProject(location, plugin.pluginName)) { project ->

            project.root.folder == plugin.root

            with(project.platform) {platform ->
                platform.folder.exists()
                platform.source().exists()
                platform.podspec().exists()
            }

            with(project.android) {android ->
                android.folder.exists()
                android.manifest().exists()
            }

            with(project.ios) {ios ->
                ios.folder.exists()
                ios.podspec().exists()
                ios.podfile().exists()
                ios.appDelegate().exists()
            }
        }

        where:
        location << [plugin.root, plugin.root.absolutePath, new Root(plugin.root)]
    }

    def "An exception is thrown when root does not exist" () {
        when:
        with(ProjectKt.klutterProject("/Fake", "")) { project ->
            // Getter will throw exception
            project.root.folder
        }

        then:
        KlutterException e = thrown()
        e.getMessage() == "The root folder does not exist: /Fake."
    }

    def "An exception is thrown when platform does not exist" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and:
        def project = ProjectKt.klutterProject(root, "")

        when:
        project.platform.source()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/platform")
    }

    def "An exception is thrown when platform does not contain a src/commonMain folder" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing platform module"
        new File("$root/platform").mkdirs()

        and:
        def project = ProjectKt.klutterProject(root, "")

        when:
        project.platform.source()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/src/commonMain")
    }

    def "An exception is thrown when platform does not contain a podspec file" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing platform module"
        new File("$root/platform").mkdirs()

        and:
        def project = ProjectKt.klutterProject(root, "plug")

        when:
        project.platform.podspec()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("plug.podspec")
    }

    def "Verify platform .podspec file is returned" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios folder"
        new File("$root/platform").mkdirs()

        and: "an existing plugin.podspec"
        def file = new File("$root/platform/plugin.podspec")
        file.createNewFile()

        and:
        def project = ProjectKt.klutterProject(root, "plugin")

        expect:
        project.platform.podspec().absolutePath == file.absolutePath
    }

    def "Verify platform src/commonMain folder is returned" () {
        when:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing platform/src/commonMain folder"
        def folder = new File("$root/platform/src/commonMain")
        folder.mkdirs()

        and:
        def project = ProjectKt.klutterProject(root, "")

        then:
        project.platform.source().absolutePath == folder.absolutePath
    }

    def "An exception is thrown when android does not exist" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and:
        def project = ProjectKt.klutterProject(root, "")

        when:
        project.android.manifest()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/android")
    }

    def "An exception is thrown when android does not contain a src/main folder" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios module"
        new File("$root/android").mkdirs()

        and:
        def project = ProjectKt.klutterProject(root, "")

        when:
        project.android.manifest()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/src/main")
    }

    def "An exception is thrown when android/src/main does not contain an AndroidManifest.xml" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios module"
        new File("$root/android/src/main").mkdirs()

        and:
        def project = ProjectKt.klutterProject(root, "")

        when:
        project.android.manifest()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/AndroidManifest.xml")
    }

    def "Verify android/src/main/AndroidManifest.xml is returned" () {
        when:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing android module"
        new File("$root/android/src/main").mkdirs()

        and:
        def file = new File("$root/android/src/main/AndroidManifest.xml")
        file.createNewFile()

        and:
        def project = ProjectKt.klutterProject(root, "")

        then:
        project.android.manifest().absolutePath == file.absolutePath
    }

    def "An exception is thrown when ios does not exist" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and:
        def project = ProjectKt.klutterProject(root, "")

        when:
        project.ios.podfile()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/ios")
    }

    def "An exception is thrown when ios does not contain a Podfile" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios module"
        new File("$root/ios").mkdirs()

        and:
        def project = ProjectKt.klutterProject(root, "")

        when:
        project.ios.podfile()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/Podfile")
    }

    def "Verify Podfile is returned" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios folder"
        new File("$root/ios").mkdirs()

        and: "an existing Podfile"
        def file = new File("$root/ios/Podfile")
        file.createNewFile()

        and:
        def project = ProjectKt.klutterProject(root, "")

        expect:
        project.ios.podfile().absolutePath == file.absolutePath
    }

    def "An exception is thrown when ios does not contain a podspec file" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios module"
        new File("$root/ios").mkdirs()

        and:
        def project = ProjectKt.klutterProject(root, "plug")

        when:
        project.ios.podspec()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("plug.podspec")
    }

    def "Verify .podspec file is returned" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios folder"
        new File("$root/ios").mkdirs()

        and: "an existing plugin.podspec"
        def file = new File("$root/ios/plugin.podspec")
        file.createNewFile()

        and:
        def project = ProjectKt.klutterProject(root, "plugin")

        expect:
        project.ios.podspec().absolutePath == file.absolutePath
    }

    def "An exception is thrown when ios does not contain a Runner folder" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios module"
        new File("$root/ios").mkdirs()

        and:
        def project = ProjectKt.klutterProject(root, "")

        when:
        project.ios.appDelegate()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/Runner")
    }

    def "An exception is thrown when ios/Runner does not contain a AppDelegate.swift file" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios/Runner folder"
        new File("$root/ios/Runner").mkdirs()

        and:
        def project = ProjectKt.klutterProject(root, "")

        when:
        project.ios.appDelegate()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/Runner/AppDelegate.swift")
    }

    def "Verify AppDelegate.swift file is returned" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios/Runner folder"
        new File("$root/ios/Runner").mkdirs()

        and: "an existing AppDelegate.swift"
        def file = new File("$root/ios/Runner/AppDelegate.swift")
        file.createNewFile()

        and:
        def project = ProjectKt.klutterProject(root, "")

        expect:
        project.ios.appDelegate().absolutePath == file.absolutePath
    }

    def "When no pluginName is given then it is retrieved from the root/pubspec.yaml"() {
        given:
        def folder = Files.createTempDirectory("").toFile()
        def root = folder.absolutePath

        and: "an existing pubspec.yaml"
        def pubspec = new File("$root/pubspec.yaml")

        and: "with plugin content"
        pubspec.createNewFile()
        pubspec.write(yaml(pluginName))

        and: "an existing ios .podspec"
        new File("$root/ios").mkdirs()
        new File("$root/ios/${pluginName}.podspec").createNewFile()

        and: "an existing platform .podspec"
        new File("$root/platform").mkdirs()
        new File("$root/platform/${pluginName}.podspec").createNewFile()

        when:
        def projectFromFile = ProjectKt.klutterProject(folder, null)
        def projectFromString = ProjectKt.klutterProject(folder.absolutePath, null)
        def projectFromRoot = ProjectKt.klutterProject(new Root(folder), null)

        then:
        projectFromFile.ios.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
        projectFromFile.platform.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")

        and:
        projectFromString.ios.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
        projectFromString.platform.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")

        and:
        projectFromRoot.ios.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
        projectFromRoot.platform.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")

        where:
        pluginName << [ "ridiculous_plugin" ]

    }

    def static yaml(String pluginName) {
        return """
            name: $pluginName
            description: A new flutter plugin project.
            version: 0.0.1
            homepage:
            
            environment:
              sdk: ">=2.16.1 <3.0.0"
              flutter: ">=2.5.0"
            
            dependencies:
              flutter:
                sdk: flutter
            
            dev_dependencies:
              flutter_test:
                sdk: flutter
            
            # For information on ...
            
            # The following section is specific to Flutter.
            flutter:
              # This ...
              plugin:
                platforms:
                  android:
                    package: some.company.ridiculous_plugin
                    pluginClass: RidiculousPlugin
                  ios:
                    pluginClass: RidiculousPlugin
        """
    }
}