package dev.buijs.klutter.core

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

import static dev.buijs.klutter.core.test.KlutterTest.*

class KlutterProjectSpec extends Specification {

    @Shared
    def plugin = plugin { project, resources -> }

    def "Verify creating a KlutterProject based of a String location"() {
        expect:
        with(create(location, plugin.pluginName)) { project ->

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
        with(create("/Fake", "")) { project ->
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
        def project = create(root, "")

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
        def project = create(root, "")

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
        def project = create(root, "plug")

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
        def project = create(root, "plugin")

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
        def project = create(root, "")

        then:
        project.platform.source().absolutePath == folder.absolutePath
    }

//    def "When no pluginName is given then it is retrieved from the root/pubspec.yaml"() {
//        given:
//
//
//        when:
//
//    }

    def "An exception is thrown when android does not exist" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and:
        def project = create(root, "")

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
        def project = create(root, "")

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
        def project = create(root, "")

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
        def project = create(root, "")

        then:
        project.android.manifest().absolutePath == file.absolutePath
    }

    def "An exception is thrown when ios does not exist" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and:
        def project = create(root, "")

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
        def project = create(root, "")

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
        def project = create(root, "")

        expect:
        project.ios.podfile().absolutePath == file.absolutePath
    }

    def "An exception is thrown when ios does not contain a podspec file" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios module"
        new File("$root/ios").mkdirs()

        and:
        def project = create(root, "plug")

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
        def project = create(root, "plugin")

        expect:
        project.ios.podspec().absolutePath == file.absolutePath
    }

    def "An exception is thrown when ios does not contain a Runner folder" () {
        given:
        def root = Files.createTempDirectory("").toFile().absolutePath

        and: "an existing ios module"
        new File("$root/ios").mkdirs()

        and:
        def project = create(root, "")

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
        def project = create(root, "")

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
        def project = create(root, "")

        expect:
        project.ios.appDelegate().absolutePath == file.absolutePath
    }

    static KlutterProject create(def location, def pluginName) {
        return KlutterProject.create(location, pluginName)
    }
}
