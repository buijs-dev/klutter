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

import 'package:klutter/src/const.dart';

import 'extensions.dart';

final _s = Platform.pathSeparator;

/// Generate a new gradle file in the flutter/tools/gradle folder
/// which will apply Klutter plugins to a Flutter project.
///
/// [Author] Gillian Buijs.
void writePluginLoaderGradleFile(String pathToFlutterSDK) => pathToFlutterSDK
    .verifyExists
    .createFlutterToolsFolder
    .absolutePath
    .createGradleFile
    .writeContent;

extension on String {
  /// Create a path to the flutter/tools/gradle/klutter_plugin_loader.gradle.kts file.
  /// If the file does not exist create it.
  File get createGradleFile => File("${this}$_s$klutterPluginLoaderGradleFile")
    ..ifNotExists((file) => File(file.absolutePath).createSync());

  /// Create a path to flutter/tools/gradle folder.
  /// If the folder does not exist create it.
  Directory get createFlutterToolsFolder =>
      Directory("${this}${_s}packages${_s}flutter_tools${_s}gradle")
        ..ifNotExists((folder) =>
            Directory(folder.absolutePath).createSync(recursive: true));
}

extension on File {
  void get writeContent {
    writeAsStringSync(
        ''' |// Copyright (c) 2021 - 2022 Buijs Software
            |//
            |// Permission is hereby granted, free of charge, to any person obtaining a copy
            |// of this software and associated documentation files (the "Software"), to deal
            |// in the Software without restriction, including without limitation the rights
            |// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
            |// copies of the Software, and to permit persons to whom the Software is
            |// furnished to do so, subject to the following conditions:
            |//
            |// The above copyright notice and this permission notice shall be included in all
            |// copies or substantial portions of the Software.//
            |// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
            |// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
            |// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
            |// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
            |// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
            |// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
            |// SOFTWARE.
            |
            |import java.io.File
            |
            |val flutterProjectRoot = rootProject.projectDir.parentFile
            |val pluginsFile = File("\$flutterProjectRoot/.klutter-plugins")
            |if (pluginsFile.exists()) {
            |  val plugins = pluginsFile.readLines().forEach { line ->
            |    val plugin = line.split("=").also {
            |      if(it.size != 2) throw GradleException("""
            |        Invalid Klutter plugin config.
            |        Check the .klutter-plugins file in the project root folder.
            |        Required format is: ':klutter:libraryname=local/path/to/flutter/cache/library/artifacts/android'
            |      """.trimIndent())
            |    }
            |
            |    val pluginDirectory = File(plugin[1]).also {
            |      if(!it.exists()) throw GradleException("""
            |        Invalid path for Klutter plugin: '\$it'.
            |        Check the .klutter-plugins file in the project root folder.
            |      """.trimIndent())
            |    }
            |
            |    include(plugin[0])
            |    project(plugin[0]).projectDir = pluginDirectory
            |
            |  }
            |}'''
            .format,
        mode: FileMode.write);
  }
}
