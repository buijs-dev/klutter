package dev.buijs.klutter.gradle.tasks


import dev.buijs.klutter.kore.KlutterTask
import spock.lang.Specification

import java.nio.file.Files

import static dev.buijs.klutter.gradle.tasks.TaskTestUtil.getTask
import static dev.buijs.klutter.gradle.tasks.TaskTestUtil.verifyTask

class KradleTasksSpec extends Specification {

    def "Verify kradlew scripts can be retrieved from resources"() {
        expect:
        KradleTasksKt.kradlew.available()
        KradleTasksKt.kradlewBat.available()
    }

    def "Verify kradlew files can be copied" () {
        given:
        def root = Files.createTempDirectory("").toFile()

        when:
        KradleTasksKt.toGetKradleTask(root).run()

        then:
        root.toPath().resolve("kradlew").toFile().exists()
        root.toPath().resolve("kradlew.bat").toFile().exists()
        root.toPath().resolve(".kradle").resolve("kradle-wrapper.jar").toFile().exists()
    }

    def "When kradle directory exists, GetKradleTask deletes all content" () {
        given:
        def root = Files.createTempDirectory("").toFile()
        def dotKradleDir = root.toPath().resolve(".kradle")
        dotKradleDir.toFile().mkdirs()
        def fakeJar = dotKradleDir.resolve("kradle-wrapper.jar").toFile()
        fakeJar.createNewFile()
        def fakeJarSize = fakeJar.size()

        when:
        def task = KradleTasksKt.toGetKradleTask(root)

        and:
        task.run()

        then: "Jar is overwritten by real kradle-wrapper.jar"
        def realJar = root.toPath().resolve(".kradle").resolve("kradle-wrapper.jar").toFile()
        realJar.exists()
        realJar.size() > fakeJarSize

        when:
        task.run()

        then: "Jar is overwritten but identical"
        with(root.toPath().resolve(".kradle").resolve("kradle-wrapper.jar").toFile()) {
            it.exists()
            it.size() == realJar.size()
        }

    }

    def "Verify GetKradleTask returns new instance of KlutterTask"() {
        given:
        def task = getTask(GetKradleTask)

        expect:
        task instanceof GetKradleTask
        task.klutterTask$gradle() instanceof KlutterTask
    }

}