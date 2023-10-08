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
package dev.buijs.klutter.kore.templates.flutter

import dev.buijs.klutter.kore.KlutterPrinter
import dev.buijs.klutter.kore.ast.AbstractType
import dev.buijs.klutter.kore.ast.CustomType
import dev.buijs.klutter.kore.ast.FlutterAsyncChannel
import dev.buijs.klutter.kore.common.toSnakeCase
import dev.buijs.klutter.kore.templates.dartType

/**
 * Example
 *
 * ```
 * import 'package:flutter/services.dart';
 * import 'package:flutter/widgets.dart';
 * import 'package:klutter_ui/klutter_ui.dart';
 * import 'package:hello/src/my_response_dataclass.dart';
 *
 * const _stream = EventChannel('com.example.hello/stream/my_response_subscriber');
 *
 * class MyResponseSubscriber extends Subscriber<MyResponse> {
 *   const MyResponseSubscriber({
 *     required Widget Function(MyResponse?) builder,
 *     Key? key,
 *   }) : super(
 *     builder: builder,
 *     channel: _stream,
 *     topic: "my_response_subscriber",
 *     key: key,
 *   );
 *
 *   @override
 *   MyResponse deserialize(String json) => json.toMyResponse;
 * }
 * ```
 */
class SubscriberWidget(
    private val topic: String,
    private val channel: FlutterAsyncChannel,
    private val controllerName: String,
    private val dataType: AbstractType,
): KlutterPrinter {

    override fun print(): String = """
            |import 'package:flutter/services.dart';
            |import 'package:flutter/widgets.dart';
            |import 'package:klutter_ui/klutter_ui.dart';
            ${if(dataType is CustomType) {
                "import '../${dataType.className.toSnakeCase()}_dataclass.dart';\n" +
                "import '../${dataType.className.toSnakeCase()}_extensions.dart';\n"
            } else ""}
            |
            |const _stream = EventChannel('${channel.name}');
            |
            |class $controllerName extends Subscriber<${dataType.dartType()}> {
            |  const ${controllerName}({
            |    required Widget Function(${dataType.dartType()}?) builder,
            |    Key? key,
            |  }) : super(
            |    builder: builder,
            |    channel: _stream,
            |    topic: "$topic",
            |    key: key,
            |  );
            |
            |  @override
            |  ${dataType.dartType()} decode(dynamic json) => 
            ${if(dataType is CustomType) {
                "(json as String).to${dataType.dartType()};"
            } else {
                "json as ${dataType.dartType()};"
            }}
            |}
            |""".trimMargin()

}