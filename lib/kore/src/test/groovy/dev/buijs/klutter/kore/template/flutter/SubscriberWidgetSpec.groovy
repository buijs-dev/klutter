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
package dev.buijs.klutter.kore.template.flutter

import dev.buijs.klutter.kore.ast.CustomType
import dev.buijs.klutter.kore.ast.StringType
import dev.buijs.klutter.kore.templates.flutter.*
import dev.buijs.klutter.kore.test.TestUtil
import spock.lang.Specification

class SubscriberWidgetSpec extends Specification {

    def "Verify a SubscriberWidget with standard response type"() {
        given:
        def widget = new SubscriberWidget(
                "my-topic",
                "MyController",
                "klutter.test/publisher",
                new StringType())

        expect:
        TestUtil.verify(widget.print(), stringResponseWidget)
    }

    def "Verify a SubscriberWidget with custom response type"() {
        given:
        def widget = new SubscriberWidget(
                "my-topic",
                "MyController",
                "klutter.test/publisher",
                new CustomType("MyType", "foo.bar.examples", []))

        expect:
        TestUtil.verify(widget.print(), customTypeResponseWidget)
    }

    def stringResponseWidget = '''
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:klutter_ui/klutter_ui.dart';
            

const _stream = EventChannel('MyController');

class klutter.test/publisher extends Subscriber<String> {
  const klutter.test/publisher({
    required Widget Function(String?) child,
    Key? key,
  }) : super(
    child: child,
    channel: _stream,
    topic: "my-topic",
    key: key,
  );

  @override
  String decode(dynamic json) => 
            json as String;
}
    '''

    def customTypeResponseWidget = '''
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:klutter_ui/klutter_ui.dart';
import '../my_type_dataclass.dart';
import '../my_type_extensions.dart';


const _stream = EventChannel('MyController');

class klutter.test/publisher extends Subscriber<MyType> {
  const klutter.test/publisher({
    required Widget Function(MyType?) child,
    Key? key,
  }) : super(
    child: child,
    channel: _stream,
    topic: "my-topic",
    key: key,
  );

  @override
  MyType decode(dynamic json) => 
            (json as String).toMyType;
}
    '''
}