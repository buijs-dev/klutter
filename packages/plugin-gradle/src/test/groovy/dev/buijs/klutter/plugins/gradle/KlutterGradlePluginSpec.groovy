package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.core.UtilsKt
import dev.buijs.klutter.core.project.Platform
import dev.buijs.klutter.core.test.TestPlugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class KlutterGradlePluginSpec extends Specification {

    def "Verify the plugin is applied"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply("dev.buijs.klutter.gradle")

        and:
        def plugin = project.plugins.getPlugin(KlutterGradlePlugin.class)

        then:
        plugin != null

    }

    def "Verify klutterGenerateAdapter task is created"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply("dev.buijs.klutter.gradle")

        then:
        def taskContainer = project.getTasks()

        and:
        taskContainer != null

        and:
        def arr = taskContainer.toArray()

        and:
        arr.size() == 2

        and:
        def task = arr[0]
        def task2 = arr[1]

        and:
        task != null
        task.toString() == "task ':klutterExcludeArchsPlatformPodspec'"

        and:
        task2 != null
        task2.toString() == "task ':klutterGenerateAdapters'"

        and:
        task.actions.size() == 1
        task2.actions.size() == 1

    }

    def "Verify KlutterGradleTask task action executes describe method"() {

        given:
        def sut = Mock(KlutterGradleTask) {
            describe() >> increment()
        }

        when:
        sut.execute()

        then:
        1 * sut.describe()

        and:
        i == 1
    }

    private def static i = 0

    private static increment() { i = 1 }

    def "Verify KlutterGradleTask project() returns ext.root if set"() {

        given:
        def plugin = new TestPlugin()

        def extension = new KlutterGradleExtension()
        extension.root = plugin.root

        def container = Mock(ExtensionContainer) {
            getByName("klutter") >> extension
        }

        def gradleProject = Mock(Project) {
            adapter() >> extension
            it.extensions >> container
        }

        and:
        def sut = Mock(KlutterGradleTask) {
            project >> gradleProject
        }

        when:
        def project = sut.project()

        then:
        project.root.folder.absolutePath == plugin.root.absolutePath

    }

    def "Verify KlutterGradleTask project() returns rootProject dir if no root is set"() {

        given:
        def plugin = new TestPlugin()

        def extension = new KlutterGradleExtension()

        def container = Mock(ExtensionContainer) {
            getByName("klutter") >> extension
        }

        def gradleRootProject = Mock(Project) {
            it.projectDir >> plugin.root
        }

        def gradleProject = Mock(Project) {
            adapter() >> extension
            it.extensions >> container
            it.rootProject >> gradleRootProject
        }

        and:
        def sut = Mock(KlutterGradleTask) {
            project >> gradleProject
        }

        when:
        def project = sut.project()

        then:
        project.root.folder.absolutePath == plugin.root.absolutePath

    }

    def "Verify ExcludeArchsPlatformPodspec task"() {

        given:
        def podspec = GroovyMock(UtilsKt) {
            it.excludeArm64(_) >> decrement()
        }

        def platform = GroovyMock(Platform) {
            it.podspec() >> podspec
        }

        def plugin = GroovyMock(dev.buijs.klutter.core.project.Project) {
            it.platform >> platform
        }

        and:
        def sut = GroovyMock(ExcludeArchsPlatformPodspec) {
            it.project() >> plugin
        }

        when:
        sut.describe()

        then:
        j == 0

    }

    private def static j = 1

    private static decrement() { j = 0 }
}