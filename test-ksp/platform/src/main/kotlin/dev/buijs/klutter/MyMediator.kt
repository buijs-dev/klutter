package dev.buijs.klutter

import dev.buijs.klutter.kompose.Mediator

//@Mediator
object MyMediator: Mediator() {

    override fun <T> onEvent(event: String, data: T?) {
        TODO("Not yet implemented")
    }

}