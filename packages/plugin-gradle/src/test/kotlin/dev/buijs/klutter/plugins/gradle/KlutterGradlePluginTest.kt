package dev.buijs.klutter.plugins.gradle

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.gradle.api.plugins.ExtensionContainer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File
import org.gradle.api.Project as GradleProject

internal class KlutterGradlePluginTest: WordSpec({

    "Verify adapter method" should {

        "Throws ISE if wrong class is found" {
            val container: ExtensionContainer = mock()
            val project: GradleProject = mock()

            whenever(container.getByName("klutter")).thenReturn("Not an extension")
            whenever(project.extensions).thenReturn(container)

            try {
                project.adapter()
            } catch(e: Exception) {
                e::class.java.simpleName shouldBe "IllegalStateException"
                e.message shouldBe "klutter extension is not of the correct type"
            }

        }

        "Return extension if correct class is found" {
            val extension: KlutterGradleExtension = mock()
            val container: ExtensionContainer = mock()
            val project: GradleProject = mock()

            whenever(container.getByName("klutter"))
                .thenReturn(extension)

            whenever(project.extensions)
                .thenReturn(container)

            project.adapter() shouldBe extension

        }

    }

    "Verify KlutterGradleExtension class" should {

        val extension = KlutterGradleExtension()

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

})