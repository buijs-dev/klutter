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
package dev.buijs.klutter.kore.template.flutter


import dev.buijs.klutter.kore.ast.StringType
import dev.buijs.klutter.kore.templates.flutter.*
import dev.buijs.klutter.kore.test.TestUtil
import spock.lang.Specification

class PublisherWidgetSpec extends Specification {

    def "Verify a PublisherWidget without message and standard response type"() {
        given:
        def widget = new PublisherWidget(
                new FlutterChannel("klutter.test/publisher"),
                new FlutterEvent("sayWhat"),
                new FlutterExtension("SayWhatNow"),
                null,
                new FlutterMessageType(new StringType()),
                new FlutterMethod("getSayWhat")
        )

        expect:
        TestUtil.verify(PublisherWidgetKt.print(widget), noRequestStringResponseWidget)
    }

    def "Verify a PublisherWidget without message and custom response type"() {


    }

    def "Verify a PublisherWidget with standard message and standard response type"() {

    }

    def "Verify a PublisherWidget with custom message and custom response type"() {


    }

    def "Verify a PublisherWidget with custom message and standard response type"() {


    }

    def "Verify a PublisherWidget with standard message and custom response type"() {

    }

    def noRequestStringResponseWidget =
              '''import 'package:flutter/services.dart';
                 import 'package:flutter/widgets.dart';
                 import 'package:klutter_ui/klutter_ui.dart';
                 
                 const MethodChannel _channel =
                   MethodChannel('klutter.test/publisher');
                   
                 extension SayWhatNow on State {
                   void getSayWhat({
                     void Function(String)? onSuccess,
                     void Function(Exception)? onFailure,
                     void Function()? onNullValue,
                     void Function(AdapterResponse<String>)? onComplete,
                   }) => doEvent<String>(
                     state: this,
                     event: "sayWhat",
                     channel: _channel,
                     
                   );
                 }
                      
                 void getSayWhat({
                 
                   State? state,
                   void Function(String)? onSuccess,
                   void Function(Exception)? onFailure,
                   void Function()? onNullValue,
                   void Function(AdapterResponse<String>)? onComplete,
                 }) => doEvent<String>(
                   state: state,
                   event: "sayWhat",
                   channel: _channel,
                   
                 );
                '''

}