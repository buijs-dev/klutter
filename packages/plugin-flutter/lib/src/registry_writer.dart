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

/// Create and/or append the .klutter-plugins file to register a Klutter plugin.
///
/// [Author] Gillian Buijs.
void registerPlugin({
  required String pathToRoot,
  required String pluginName,
  required String pluginLocation,
}) =>
    createRegistry(pathToRoot).append(pluginName, pluginLocation);

/// Create registry file .klutter-plugins.
///
/// [Author] Gillian Buijs.
File createRegistry(String pathToRoot) =>
    pathToRoot.verifyExists.toKlutterPlugins;

extension on String {
  /// Create a path to the root-project/.klutter-plugins file.
  /// If the file does not exist create it.
  File get toKlutterPlugins =>
      File("${this}${Platform.pathSeparator}.klutter-plugins")
        ..ifNotExists((file) => File(file.absolutePath).createSync());
}

extension on File {
  /// Write the plugin name and location to the .klutter-plugins file.
  ///
  /// If there already is a plugin registered with the given name
  /// then the location is updated.
  void append(String name, String location) {
    // Will be set to true if a registry for the given name is found.
    var hasKey = false;

    final lines = readAsLinesSync().map((line) {
      // Split 'key=value' and compare key with given name.
      final key = line.substring(0, line.indexOf("=")).trim();

      if (key == name.trim()) {
        // Set new location for the library name.
        hasKey = true;
        return "$name=$location";
      } else {
        // Return the registry as-is.
        return line;
      }
    }).toList();

    // If true then registry is already updated for given name.
    if (!hasKey) lines.add("$name=$location");

    writeAsStringSync(lines.join("\n"), mode: FileMode.write);
  }
}
