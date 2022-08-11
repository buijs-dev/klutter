package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.shared.KomposeControllerBuilderKt
import spock.lang.Specification

import java.nio.file.Files

class KomposeControllerBuilderSpec extends Specification {

    def "Verify KomposeControllerBuilder returns correct Controller name"() {

        given:
        def file = Files.createTempFile("", "").toFile()

        and:
        file.write(clazz)

        when:
        def controller = KomposeControllerBuilderKt.toControllerNames([file]).first()

        then:
        controller == "dev.buijs.klutter.kompose_app.platform.GreetingController"

        where:
        clazz = """
            package dev.buijs.klutter.kompose_app.platform

            import dev.buijs.klutter.annotations.*
            import dev.buijs.klutter.annotations.KomposeState.*
            import dev.buijs.klutter.ui.controller.KomposeController
            import dev.buijs.klutter.ui.event.KlutterEvent
            import kotlinx.serialization.Serializable
            
            @Controller
            class GreetingController: KomposeController<Greeting123>(
                state = Greeting123(
                    count = 0,
                    greeting = "Hello World!!!",
                )
            ) {
            
                override fun onEvent(event: String, data: Any?) {
                    when(event) {
                        GreetingSetMessage::class.qualifiedName -> {
                            state = Greeting123(
                                count = state.count,
                                greeting = data as String,
                            )
                        }
            
                        GreetingIncrementCount::class.qualifiedName -> {
                            state = Greeting123(
                                count = state.count + 1,
                                greeting = state.greeting,
                            )
                        }
            
                        else -> { }
                    }
                }
            
            }
         """

    }

}
