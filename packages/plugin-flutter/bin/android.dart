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

import 'package:klutter/src/extensions.dart';
import 'package:klutter/src/klutter_exception.dart';
import 'package:klutter/src/local_properties_locator.dart';
import 'package:klutter/src/plugins_gradle_generator.dart';
import 'package:klutter/src/registry_writer.dart';
import 'package:klutter/src/settings_gradle_visitor.dart';

final _s = Platform.pathSeparator;

const _red = "\x1B[31m";
const _green = "\x1B[32m";

/// Generate a new apple_keys.json template in the working directory.
///
///[Author] Gillian Buijs.
Future<void> main(List<String> args) async {

  print('''$_green
  ════════════════════════════════════════════
     KLUTTER (v0.1.0)                               
  ════════════════════════════════════════════
  ''');

  final root = Directory.current.absolutePath;
  final android = "$root${_s}android";
  final sdk = flutterSDK(android);

  try {
    writePluginLoaderGradleFile(sdk);
    createRegistry(root);
    applyPluginLoader(android);
  } on KlutterException catch(e) {
    return print('${_red}KLUTTER: $e.cause'.format);
  }

  print('${_green}KLUTTER: Android setup complete! Project is ready to use Klutter plugins.'.format);
}
