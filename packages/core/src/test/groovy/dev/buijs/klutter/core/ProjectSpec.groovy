package dev.buijs.klutter.core

import dev.buijs.klutter.core.project.ProjectKt
import dev.buijs.klutter.core.project.Root
import dev.buijs.klutter.core.test.TestPlugin
import spock.lang.Shared
import spock.lang.Specification

class ProjectSpec extends Specification {

    @Shared
    def plugin = new TestPlugin()

    def "Verify creating a dev.buijs.klutter.core.Project based of a String location"() {
        given:
        def project = ProjectKt.plugin(location, plugin.pluginName)

        expect:
        project.root.folder == plugin.root

        with(project.platform) {platform ->
            platform.folder.exists()
            platform.source().exists()
            platform.podspec().exists()
        }

        with(project.android) {android ->
            android.folder.exists()
            android.manifest.exists()
        }

        with(project.ios) {ios ->
            ios.folder.exists()
            ios.podspec().exists()
            ios.podfile().exists()
            ios.appDelegate().exists()
        }

        where:
        location << [plugin.root, plugin.root.absolutePath]
    }

    def "Verify creating a dev.buijs.klutter.core.Project based of a String location without plugin name"() {
        given:
        def project = ProjectKt.plugin(location)

        expect:
        project.root.folder == plugin.root

        with(project.platform) {platform ->
            platform.folder.exists()
            platform.source().exists()
            platform.podspec().exists()
        }

        with(project.android) {android ->
            android.folder.exists()
            android.manifest.exists()
        }

        with(project.ios) {ios ->
            ios.folder.exists()
            ios.podspec().exists()
            ios.podfile().exists()
            ios.appDelegate().exists()
        }

        where:
        location << [plugin.root, plugin.root.absolutePath]
    }

    def "An exception is thrown when root does not exist" () {
        when:
        with(ProjectKt.plugin("/Fake", "")) { project ->
            // Getter will throw exception
            project.root.folder
        }

        then:
        KlutterException e = thrown()
        e.getMessage() == "The root folder does not exist: /Fake."
    }

    def "An exception is thrown when platform does not exist" () {
        given:
        def plugin = new TestPlugin()

        and:
        plugin.platform.deleteDir()

        and:
        def project = ProjectKt.plugin(plugin.root, "")

        when:
        project.platform.source()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/platform")
    }

    def "An exception is thrown when platform does not contain a src/commonMain folder" () {
        given:
        def plugin = new TestPlugin()

        and:
        def project = ProjectKt.plugin(plugin.root, "")

        and:
        plugin.platformCommonMain.deleteDir()

        when:
        project.platform.source()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/src/commonMain")
    }

    def "An exception is thrown when platform does not contain a podspec file" () {
        given:
        def plugin = new TestPlugin()

        and:
        def project = ProjectKt.plugin(plugin.root, "plug")

        and:
        plugin.platformPodSpec.delete()

        when:
        project.platform.podspec()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("plug.podspec")
    }

    def "Verify platform .podspec file is returned" () {
        given:
        def project = ProjectKt.plugin(plugin.root, plugin.pluginName)

        expect:
        project.platform.podspec().absolutePath == plugin.platformPodSpec.absolutePath
    }

    def "Verify platform src/commonMain folder is returned" () {
        when:
        def project = ProjectKt.plugin(plugin.root, "")

        then:
        project.platform.source().absolutePath == plugin.platformCommonMain.absolutePath
    }

    def "An exception is thrown when android does not exist" () {
        given:
        def plugin = new TestPlugin()

        and:
        plugin.android.deleteDir()

        when:
        ProjectKt.plugin(plugin.root, "")

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/android")
    }

    def "An exception is thrown when android does not contain a src/main folder" () {
        given:
        def plugin = new TestPlugin()

        and:
        plugin.androidSrcMain.deleteDir()

        when:
        def project = ProjectKt.plugin(plugin.root, "")

        and:
        project.android.manifest

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/src/main")
    }

    def "An exception is thrown when android/src/main does not contain an AndroidManifest.xml" () {
        given:
        def plugin = new TestPlugin()

        and:
        plugin.manifest.delete()

        when:
        def project = ProjectKt.plugin(plugin.root, "")

        and:
        project.android.manifest

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/AndroidManifest.xml")
    }

    def "Verify android/src/main/AndroidManifest.xml is returned" () {
        when:
        def project = ProjectKt.plugin(plugin.root, "")

        then:
        project.android.manifest.absolutePath == plugin.manifest.absolutePath
    }

    def "An exception is thrown when ios does not exist" () {
        given:
        def plugin = new TestPlugin()

        and:
        plugin.ios.deleteDir()

        when:
        def project = ProjectKt.plugin(plugin.root, "")

        and:
        project.ios.podfile()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/ios")
    }

    def "An exception is thrown when ios does not contain a Podfile" () {
        given:
        def plugin = new TestPlugin()

        and:
        plugin.podfile.delete()

        when:
        def project = ProjectKt.plugin(plugin.root, "")

        and:
        project.ios.podfile()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/Podfile")
    }

    def "Verify Podfile is returned" () {
        given:
        def project = ProjectKt.plugin(plugin.root, "")

        expect:
        project.ios.podfile().absolutePath == plugin.podfile.absolutePath
    }

    def "An exception is thrown when ios does not contain a podspec file" () {
        given:
        def plugin = new TestPlugin()

        and:
        plugin.iosPodspec.delete()

        when:
        def project = ProjectKt.plugin(plugin.root, "")

        and:
        project.ios.podspec()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith(".podspec")
    }

    def "Verify .podspec file is returned" () {
        given:
        def project = ProjectKt.plugin(plugin.root, plugin.pluginName)

        expect:
        project.ios.podspec().absolutePath == plugin.iosPodspec.absolutePath
    }

    def "An exception is thrown when ios does not contain a Runner folder" () {
        given:
        def plugin = new TestPlugin()

        and:
        plugin.runnerFolder.deleteDir()

        when:
        def project = ProjectKt.plugin(plugin.root, "")

        and:
        project.ios.appDelegate()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/Runner")
    }

    def "An exception is thrown when ios/Runner does not contain a AppDelegate.swift file" () {
        given:
        def plugin = new TestPlugin()

        and:
        plugin.appDelegate.delete()

        when:
        def project = ProjectKt.plugin(plugin.root, "")

        and:
        project.ios.appDelegate()

        then:
        KlutterException e = thrown()
        e.getMessage().startsWith("Path does not exist: ")
        e.getMessage().endsWith("/Runner/AppDelegate.swift")
    }

    def "Verify AppDelegate.swift file is returned" () {
        given:
        def project = ProjectKt.plugin(plugin.root, "")

        expect:
        project.ios.appDelegate().absolutePath == plugin.appDelegate.absolutePath
    }

    def "When no pluginName is given then it is retrieved from the root/pubspec.yaml"() {

        when:
        def projectFromFile = ProjectKt.plugin(plugin.root)
        def projectFromString = ProjectKt.plugin(plugin.root.absolutePath)

        then:
        projectFromFile.ios.podspec().absolutePath.endsWith("super_awesome.podspec")
        projectFromFile.platform.podspec().absolutePath.endsWith("super_awesome.podspec")

        and:
        projectFromString.ios.podspec().absolutePath.endsWith("super_awesome.podspec")
        projectFromString.platform.podspec().absolutePath.endsWith("super_awesome.podspec")

        where:
        pluginName << [ "super_awesome" ]

    }

}