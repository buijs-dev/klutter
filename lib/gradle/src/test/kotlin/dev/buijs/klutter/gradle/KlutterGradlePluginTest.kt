package dev.buijs.klutter.gradle

import com.google.devtools.ksp.gradle.KspExtension
import dev.buijs.klutter.gradle.dsl.KlutterExtension
import dev.buijs.klutter.gradle.tasks.klutterExtension
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File
import java.nio.file.Files
import org.gradle.api.Project as GradleProject

internal class KlutterGradlePluginTest: WordSpec({

    "Verify adapter method" should {

        "Throws ISE if wrong class is found" {
            val container: ExtensionContainer = mock()
            val project: GradleProject = mock()

            whenever(container.getByName("klutter")).thenReturn("Not an extension")
            whenever(project.extensions).thenReturn(container)

            try {
                project.klutterExtension()
            } catch(e: Exception) {
                e::class.java.simpleName shouldBe "IllegalStateException"
                e.message shouldBe "klutter extension is not of the correct type"
            }

        }

        "Return extension if correct class is found" {
            val extension: KlutterExtension = mock()
            val container: ExtensionContainer = mock()
            val project: GradleProject = mock()

            whenever(container.getByName("klutter"))
                .thenReturn(extension)

            whenever(project.extensions)
                .thenReturn(container)

            project.klutterExtension() shouldBe extension

        }

    }

    "Verify KlutterGradleExtension class" should {
        val temp = Files.createTempDirectory("").toFile()
        val subTemp = temp.resolve("foo")
        subTemp.mkdir()

        val project: org.gradle.api.Project = mock {
            whenever(it.buildDir).thenAnswer {  temp }
            whenever(it.projectDir).thenAnswer {  subTemp }
        }

        val extension = KlutterExtension(project)

        "All values default to null" {
            extension.root shouldBe null
            extension.plugin shouldBe null
        }

        "After using the plugin DSL values should be set" {

            extension.root = File("")

            extension.plugin {
                name = "With great power..."
            }

            extension.root shouldNotBe null
            extension.plugin shouldNotBe null
            extension.plugin!!.name shouldBe "With great power..."
        }

    }


 "Verify KSP extension is fed by Klutter extension through Klutter Gradle Plugin" should {

        // fake Gradle project
        val project: GradleProject = mock()
        val klutterScanFolder = Files.createTempDirectory("x").toFile()
        val klutterOutputFolder = Files.createTempDirectory("y").toFile()
        whenever(project.buildDir).thenReturn(klutterScanFolder)
        whenever(project.projectDir).thenReturn(klutterOutputFolder.resolve("foo"))
        whenever(project.logger).thenReturn(mock())

        // task container which contains the 'build' task
        val taskContainer: TaskContainer = mock()
        whenever(project.tasks).thenReturn(taskContainer)
        whenever(taskContainer.getByName("build")).thenReturn(mock())

        // plugin container which contains the ksp plugin
        val pluginContainer: PluginContainer = mock()
        whenever(project.plugins).thenReturn(pluginContainer)

        // dependency handler mock for containing the compiler-plugin dependency
        val dependencyHandler: DependencyHandler = mock()
        whenever(project.dependencies).thenReturn(dependencyHandler)

        // feeder!
        //val klutterExtension = KlutterExtension(project)

        // nomnommer!
        val kspExtension = KspExtension()

        val container: ExtensionContainer = mock()
        whenever(container.getByType(KspExtension::class.java)).thenReturn(kspExtension)
        whenever(project.extensions).thenReturn(container)
    }
})