// Copyright (c) 2021 - 2022 Buijs Software
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

import 'dart:io';
import 'extensions.dart';
import 'package:klutter/src/klutter_exception.dart';

/// Get the path to the local Flutter SDK installation
/// as configured in the root-project/android/local.properties folder.
///
/// Either:
/// - throws [KlutterException] if unsuccessful or
/// - returns [String] path to Flutter SDK installation.
///
/// [Author] Gillian Buijs.
String flutterSDK(String pathToAndroid) =>
    pathToAndroid.verifyExists.toPropertiesFile.read.property("flutter.sdk");

extension on String {
  /// Create a path to the root-project/android/local.properties file.
  /// If the file does not exist throw a [KlutterException].
  File get toPropertiesFile =>
      File("${this}${Platform.pathSeparator}local.properties")
        ..ifNotExists((_) => throw KlutterException(
            "Missing local.properties file in directory: ${this}"));
}

extension on File {
  /// Read the content of a properties File and
  /// return a Map<String, String> with property key and property value.
  Map<String, String> get read => readAsLinesSync()
      .map((line) => line.split("="))
      .where((line) => line.length == 2)
      .map((line) => line.map((e) => e.trim()).toList())
      .fold({}, (properties, line) => properties..addAll({line[0]: line[1]}));
}

extension on Map<String, String> {
  /// Get property (uppercase) from key-value map or throw [KlutterException] if not present.
  String property(String key) => containsKey(key)
      ? this[key]!
      : throw KlutterException(
          "Missing property '$key' in local.properties file.",
        );
}
