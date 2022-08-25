package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.test.TestResource
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import java.nio.file.Files

class MethodTest {

    @Test
    fun testMethodWithDefaultArgs() {
        // given:
        val file = Files.createTempFile("SomeClass", "kt").toFile()
        TestResource().copy("platform_source_code", file.absolutePath)

        //expect:
        file.toMethods().isEmpty() shouldBe false
    }

}