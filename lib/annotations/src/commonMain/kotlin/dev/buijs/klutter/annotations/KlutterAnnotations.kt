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
package dev.buijs.klutter.annotations

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

/**
 * Annotation which denotes the annotated function as part
 * of the Flutter - KMP interface.
 * </br>
 * When a function with this annotation is scanned,
 * a method channel call delegation will be generated.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
expect annotation class Event(
    val name: String,
)

/**
 * Annotation for data classes in the KMP module which are possibly used
 * by functions annotated with [dev.buijs.klutter.annotations.Event].
 *
 * Dart classes will be generated to enable typesafe communication
 * between Flutter and KMP.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
expect annotation class Response()

/**
 * Annotation for class which require an instance of Android Context.
 *
 * When a class is annotated with this annotation then an instance
 * of Android Context is passed to the class constructor in the generated
 * Android adapter.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
expect annotation class AndroidContext()

/**
 * Parent Type for all custom classes which are passed between Flutter and KMP.
 */
@Serializable
abstract class KlutterJSON<T> {

    fun toKJson() = Json.encodeToString(strategy(), data())

    abstract fun data(): T

    abstract fun strategy(): SerializationStrategy<T>

}

/**
 * All methods contained by a class annotated with [Controller] will be
 * added to the generated iOS/Android adapters.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
expect annotation class Controller(
    val type: ControllerType = ControllerType.Default,
)

enum class ControllerType {
    /**
     * Only one instance of the controller is made during the app's lifecycle.
     */
    Singleton,

    /**
     * A new instance of the controller is made everytime an event is triggerd.
     */
    RequestScoped,

    /**
     * The klutter framework chooses between [Singleton] and [RequestScoped]
     * depending on the type of controller.
     * </br>
     * <ul>
     *     <li>For publishers: Singleton.</li>
     *     <li>For subscribers: RequestScoped.</li>
     * </ul>
     */
    Default,
}