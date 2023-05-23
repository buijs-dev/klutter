/* Copyright (c) 2021 - 2023 Buijs Software
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

package dev.buijs.klutter.kore.template

import dev.buijs.klutter.kore.templates.UtilitiesKt
import spock.lang.Specification

class UtilitiesSpec extends Specification {

    def "Verify appending a String Template preserves margin"() {
        given:
        def stringBuilder = new StringBuilder()

        when:
        UtilitiesKt.appendTemplate(stringBuilder, template)

        then:
        with(stringBuilder.toString().readLines()) {
            it.any {it == '''    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {''' }
            it.any {it == '''        this.methodChannels = methodChannelNames.map { name ->''' }
            it.any {it == '''            val channel = MethodChannel(binding.binaryMessenger, name)''' }
            it.any {it == '''            channel.setMethodCallHandler(this)''' }
            it.any {it == '''            channel''' }
            it.any {it == '''        }''' }
            it.any {it == '''''' }
            it.any {it == '''        this.eventChannels = eventChannelNames.map { name ->''' }
            it.any {it == '''            val channel = EventChannel(binding.binaryMessenger, name)''' }
            it.any {it == '''            channel.setStreamHandler(this)''' }
            it.any {it == '''            channel''' }
            it.any {it == '''        }''' }
            it.any {it == '''   }''' }
        }

        where:
        template = """
                    |    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
                    |        this.methodChannels = methodChannelNames.map { name ->
                    |            val channel = MethodChannel(binding.binaryMessenger, name)
                    |            channel.setMethodCallHandler(this)
                    |            channel
                    |        }
                    |
                    |        this.eventChannels = eventChannelNames.map { name ->
                    |            val channel = EventChannel(binding.binaryMessenger, name)
                    |            channel.setStreamHandler(this)
                    |            channel
                    |        }
                    |   }
                    |"""

    }

}