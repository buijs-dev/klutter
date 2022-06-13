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

package dev.buijs.klutter.core.template

import dev.buijs.klutter.core.CoreTestUtil
import dev.buijs.klutter.core.TestData
import dev.buijs.klutter.core.templates.IosAdapter
import spock.lang.Specification

class IosAdapterSpec extends Specification {

    def "IosAdapter should create a valid Swift class"() {
        given:
        def pluginName = "SuperAwesomePlugin"
        def channelName = "foo.bar.super_awesome"
        def methods = TestData.complexityMethods

        and: "The printer as SUT"
        def adapter = new IosAdapter(pluginName, channelName, methods)

        expect:
        CoreTestUtil.verify(adapter, classBody)

        where:
        classBody = """
            import Flutter
            import UIKit
            import Platform
            
            public class SwiftSuperAwesomePlugin: NSObject, FlutterPlugin {
              public static func register(with registrar: FlutterPluginRegistrar) {
                let channel = FlutterMethodChannel(name: "foo.bar.super_awesome", binaryMessenger: registrar.messenger())
                let instance = SwiftSuperAwesomePlugin()
                registrar.addMethodCallDelegate(instance, channel: channel)
              }
            
              public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
                switch call.method {
                    case "doFooBar":
                        self.doFooBar(result: result)
                    case "notDoFooBar":
                        self.notDoFooBar(result: result)
                    case "complexityGetter":
                        self.complexityGetter(result: result)
                    default:
                        result(FlutterMethodNotImplemented)
                }
              }
            
                func doFooBar(result: @escaping FlutterResult) {
                    result(FooBar().zeta())
                }
            
                func notDoFooBar(result: @escaping FlutterResult) {
                    result(FooBar().beta())
                }
            
                func complexityGetter(result: @escaping FlutterResult) {
                    ComplexFoo().bar { data, error in
                        if let response = data { result(response.toKJson()) }
            
                        if let failure = error { result(failure) }
                 }
                 }
                }"""
    }

}