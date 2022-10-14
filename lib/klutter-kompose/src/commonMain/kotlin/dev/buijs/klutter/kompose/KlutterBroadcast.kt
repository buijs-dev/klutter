/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package dev.buijs.klutter.kompose

import dev.buijs.klutter.annotations.KlutterJSON
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Controller which sends messages of type <T> which
 * can be received on the Dart side by EventChannel listeners.
 */
abstract class KlutterBroadcast<T: KlutterJSON<*>>(
    /**
     * Scope in which the [broadcast] function is executed.
     */
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
){

    /**
     * The flow which exposes messages of type <T>.
     */
    private val _events = MutableSharedFlow<T>()

    init {
        scope.launch { broadcast() }
    }

    /**
     * Add events to be broadcast.
     */
    suspend fun emit(event: T) {
        _events.emit(event)
    }

    /**
     * Subscribe to the broadcast and receive messages.
     */
    fun receiveBroadcastAndroid() =
        _events.asSharedFlow()

    /**
     * Subscribe to the broadcast and receive messages.
     *
     * Flow<T> is wrapped in [IosFlow] for easing consumption on the iOS side.
     */
    fun receiveBroadcastIOS() =
        IosFlow(_events.asSharedFlow())

    /**
     * Cancel the broadcast.
     */
    fun cancel() = scope.cancel()

    /**
     * Actual broadcast implementation which should use [emit] to
     * broadcast messages of type <T>.
     */
    abstract suspend fun broadcast()

}