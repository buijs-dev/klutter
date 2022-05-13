package dev.buijs.klutter.annotations.kmp

import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 * @author Gillian Buijs
 */
class SomeTest {
    @Test
    fun shouldTest() {

        assertEquals("""{"bar":"boo"}""", Foo(bar = "boo").toKJson())

    }
}

@Serializable
class Foo(val bar: String): KlutterJSON<Foo>(){

    override fun data() = this

    override fun strategy() = serializer()

}