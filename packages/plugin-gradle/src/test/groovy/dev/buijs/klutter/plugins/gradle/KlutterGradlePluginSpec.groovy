package dev.buijs.klutter.plugins.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Ignore
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

}