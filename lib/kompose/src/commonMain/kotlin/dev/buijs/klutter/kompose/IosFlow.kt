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

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class IosFlow<T>(source: Flow<T>): Flow<T> by source {

    fun collect(
        onEach: (T) -> Unit,
        onCompletion: (cause: Throwable?) -> Unit,
    ) = object : Cancellable {

        private val scope = CoroutineScope(
            SupervisorJob() + Dispatchers.Main
        )

        init {
            scope.launch { collect() }
        }

        private suspend fun collect() {
            try {
                collect { onEach(it) }
                onCompletion(null)
            } catch (e: Throwable) {
                onCompletion(e)
            }
        }

        override fun cancel() = scope.cancel()

    }
}

interface Cancellable {
    fun cancel()
}