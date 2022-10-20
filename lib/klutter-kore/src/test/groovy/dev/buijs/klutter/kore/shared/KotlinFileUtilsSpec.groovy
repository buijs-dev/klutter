package dev.buijs.klutter.kore.shared


import dev.buijs.klutter.kore.test.TestUtil
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files

@Ignore //TODO delete or fix?
class KotlinFileUtilsSpec extends Specification {

    def "Verify splitting a Kotlin Class File to multiple classes"() {

        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def bodies = KotlinFileUtilsKt.find(file)

        then:
        bodies.size() == 2
        TestUtil.verify(bodies[0], expectedBody1)
        TestUtil.verify(bodies[1], expectedBody2)

        where:
        classBody = """
            package some.company.ridiculous_plugin.platform

            import dev.buijs.klutter.annotations.kmp.KlutterAdaptee

            class Foo(private val my: String) {

                @KlutterAdaptee(name = "getFoo")
                fun foo1(): String {
                    return "ola!"
                }

                @KlutterAdaptee(name = "getFoo2")
                fun foo2(): Int = 1

                @KlutterAdaptee(name = "getFoo3")
                fun foo3(): Boolean =
                    foo1() == "ola!" && foo2() != 1
                            && "pizza" == "yummy"

                val y = ""

                fun x() {

                }
            }

            fun x() = ""

            /// Comments bla bla
            class Bar {

                @KlutterAdaptee(name = "getBar")
                fun bar(): String {
                    return "ola!"
                }

            }"""

        expectedBody1 = """class Foo(private val my: String) {
            
                                @KlutterAdaptee(name = "getFoo")
                                fun foo1(): String {
                                    return "ola!"
                                }
                            
                                @KlutterAdaptee(name = "getFoo2")
                                fun foo2(): Int = 1
                                
                                @KlutterAdaptee(name = "getFoo3")
                                fun foo3(): Boolean = 
                                    foo1() == "ola!" && foo2() != 1
                                            && "pizza" == "yummy"
                                
                                val y = ""
                                
                                fun x() {
                                    
                                }
                            }"""

        expectedBody2 = """class Bar {
            
                            @KlutterAdaptee(name = "getBar")
                            fun bar(): String {
                                return "ola!"
                            }
                            
                        }"""
    }

}
