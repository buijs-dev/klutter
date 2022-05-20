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

import 'package:test/test.dart';
import 'package:klutter/src/klutter_exception.dart';
import 'package:klutter/src/local_properties_locator.dart';

/// [Author] Gillian Buijs.
void main() {

  final s = Platform.pathSeparator;
  late final Directory root;
  late final Directory android;
  late final File properties;

  setUpAll(() {

    root = Directory("${Directory.current.absolute.path}${s}propslocatortest")
      ..createSync();

    android = Directory("${root.absolute.path}${s}android")
      ..createSync();

    //Will be created during test!
    properties = File("${android.absolute.path}${s}local.properties");

  });

  test('Verify exception is thrown if root/android does not exist', () {
    expect(() => flutterSDK("fake"), throwsA(predicate((e) =>
    e is KlutterException &&
        e.cause.startsWith("Path does not exist:") &&
        e.cause.endsWith("/fake"))));
  });

  test('Verify exception is thrown if root/android/local.properties does not exist', () {
    expect(() => flutterSDK(android.path), throwsA(predicate((e) =>
    e is KlutterException &&
        e.cause == "Missing local.properties file in directory: ${android.absolute.path}")));
  });

  test('Verify exception is thrown if property key does not exists', () {
    // Given an empty properties file
    properties.createSync();

    // An exception is thrown
    expect(() => flutterSDK(android.path), throwsA(predicate((e) =>
    e is KlutterException &&
        e.cause == "Missing property 'flutter.sdk' in local.properties file.")));
  });

  test('Verify property value is returned if property key exists', () {
    properties.writeAsStringSync('''
      sdk.dir=/Users/chewbacca/Library/Android/sdk
      flutter.sdk=/Users/chewbacca/tools/flutter
      flutter.versionName=1.0.0
      flutter.versionCode=1
      foo
      bar=
    ''');

    expect(flutterSDK(android.path), "/Users/chewbacca/tools/flutter");
  });

  tearDownAll(() => root.deleteSync(recursive: true));
}
