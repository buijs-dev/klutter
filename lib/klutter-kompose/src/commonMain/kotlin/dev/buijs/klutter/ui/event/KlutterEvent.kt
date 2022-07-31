package dev.buijs.klutter.ui.event

import dev.buijs.klutter.ui.controller.NoController

abstract class KlutterEvent<T, R>

/**
 * Empty event for Kompose classes which do not fire events.
 */
abstract class NoEvent: KlutterEvent<Empty, NoController>()

object Empty