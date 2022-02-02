package dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter
import dev.buijs.klutter.core.MethodCallDefinition
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.FlutterAdapterPrinter
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 */
class FlutterAdapterPrinterTest : WordSpec({

    "Using the FlutterAdapterPrinter" should {
        "Create the body GeneratedAdapter body with a branch for each KlutterAdaptee annotation" {

            val definitions = listOf(
                MethodCallDefinition(
                    call = "FooBar().zeta()",
                    getter = "doFooBar",
                    import = "io.foo.bar.FooBar",
                    returns = "String"),
                MethodCallDefinition(
                    call = "FooBar().beta()",
                    getter = "notDoFooBar",
                    import = "io.foo.bar.FooBar",
                    returns = "int"),
                MethodCallDefinition(
                    call = "ComplexFoo().bar()",
                    getter = "complexityGetter",
                    import = "io.foo.bar.ComplexFoor",
                    returns = "List<Complex>")
            )

            val actual = FlutterAdapterPrinter(definitions).print()

            actual.filter { !it.isWhitespace() } shouldBe """
                import 'dart:async';
                import 'messages.dart';
                import 'package:flutter/services.dart';
                
                /// Autogenerated by Klutter Framework. 
                /// 
                /// Do net edit directly, but recommended to store in VCS.
                /// 
                /// Adapter class which handles communication with the KMP library.
                class Adapter {
                
                  static const MethodChannel _channel = MethodChannel('KLUTTER');
                
                  static Future<AdapterResponse<String>> get doFooBar async {
                    try {
                      final json = await _channel.invokeMethod('doFooBar');
                      return AdapterResponse.success(json.toString());
                    } catch (e) {
                      return AdapterResponse.failure(e as Exception);
                    }
                  }
                
                  static Future<AdapterResponse<int>> get notDoFooBar async {
                    try {
                      final json = await _channel.invokeMethod('notDoFooBar');
                      return AdapterResponse.success(json.toInt());
                    } catch (e) {
                      return AdapterResponse.failure(e as Exception);
                    }
                  }
                
                  static Future<AdapterResponse<List<Complex>>> get complexityGetter async {
                    try {
                      final json = await _channel.invokeMethod('complexityGetter');
                      return AdapterResponse.success(List<Complex>.from(json.decode(json).map((o) => Complex.fromJson(o))));
                    } catch (e) {
                      return AdapterResponse.failure(e as Exception);
                    }
                  }
                
                }
                
                /// Autogenerated by Klutter Framework. 
                /// 
                /// Do net edit directly, but recommended to store in VCS.
                /// 
                /// Wraps an [exception] if calling the platform method has failed to be logged by the consumer.
                /// Or wraps an [object] of type T when platform method has returned a response and
                /// deserialization was successful.
                class AdapterResponse<T> {
                
                  AdapterResponse(this._object, this._exception);
                
                  factory AdapterResponse.success(T t) => AdapterResponse(t, null);
                
                  factory AdapterResponse.failure(Exception e) => AdapterResponse(null, e);
                
                  ///The actual object to returned
                  T? _object;
                    set object(T object) => _object = object;
                    T get object => _object!;
                
                  ///Exception which occurred when calling a platform method failed.
                  Exception? _exception;
                    set exception(Exception e) => _exception = e;
                    Exception get exception => _exception!;
                
                  bool isSuccess() {
                    return _object != null;
                  }
                   
                }
                """.filter { !it.isWhitespace() }
        }
    }
})