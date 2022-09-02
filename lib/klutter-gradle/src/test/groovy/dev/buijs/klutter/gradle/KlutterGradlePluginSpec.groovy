package dev.buijs.klutter.gradle


import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class KlutterGradlePluginSpec extends Specification {

    def "Verify the plugin is applied"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply("dev.buijs.klutter")

        and:
        def plugin = project.plugins.getPlugin(KlutterGradlePlugin.class)

        then:
        plugin != null

    }

    def "Verify klutterGenerateAdapter task is created"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply("dev.buijs.klutter")

        then:
        def taskContainer = project.getTasks()

        and:
        taskContainer != null

        and:
        def arr = taskContainer.toArray()

        and:
        arr.size() != 0

    }
}