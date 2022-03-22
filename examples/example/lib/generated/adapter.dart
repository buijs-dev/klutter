import 'dart:async';
import 'messages.dart';
import 'package:flutter/services.dart';

/// Autogenerated by Klutter Framework. 
/// 
/// Do net edit directly, but recommended to store in VCS.
/// 
/// Adapter class which handles communication with the Platform library.
class Adapter {

  static const MethodChannel _channel = MethodChannel('KLUTTER');

  static Future<AdapterResponse<String>> get getGreeting async {
    try {
      final json = await _channel.invokeMethod('getGreeting');
      return AdapterResponse.success(json.toString());
    } catch (e) {
      return AdapterResponse.failure(e as Exception);
    }
  }

  static Future<AdapterResponse<ExtensiveGreetingInfo>> get getExtensiveGreeting async {
    try {
      final json = await _channel.invokeMethod('getExtensiveGreeting');
      return AdapterResponse.success(ExtensiveGreetingInfo.fromJson(json));
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