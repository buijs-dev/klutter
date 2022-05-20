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
import 'package:klutter/src/plugins_gradle_generator.dart';

/// [Author] Gillian Buijs.
void main() {

  final s = Platform.pathSeparator;
  late final Directory root;
  late final File pluginLoader;

  setUpAll(() {

    root = Directory("${Directory.current.absolute.path}${s}settingsgradletest")
      ..createSync();

    //Will be created during test!
    pluginLoader = File("${root.absolute.path}"
        "${s}packages${s}flutter_tools${s}gradle${s}klutter_plugin_loader.gradle.kts");

  });

  test('Verify exception is thrown if root does not exist', () {
    expect(() => writePluginLoaderGradleFile("fake"), throwsA(predicate((e) =>
        e is KlutterException &&
        e.cause.startsWith("Path does not exist:") &&
        e.cause.endsWith("/fake"))));
  });

  test('Verify a new Gradle file is created if it does not exist', () {

    expect(pluginLoader.existsSync(), false, reason: "file should not exist");

    writePluginLoaderGradleFile(root.path);

    expect(pluginLoader.readAsStringSync().replaceAll(" ", "").replaceAll("\n", ""),
        ''' // Copyright (c) 2021 - 2022 Buijs Software
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
            import java.io.File
            
            val flutterProjectRoot = rootProject.projectDir.parentFile
            val pluginsFile = File("\$flutterProjectRoot/.klutter-plugins")
            if (pluginsFile.exists()) {
              val plugins = pluginsFile.readLines().forEach { line ->
                val plugin = line.split("=").also {
                  if(it.size != 2) throw GradleException("""
                    Invalid Klutter plugin config.
                    Check the .klutter-plugins file in the project root folder.
                    Required format is: ':klutter:libraryname=local/path/to/flutter/cache/library/artifacts/android'
                  """.trimIndent())
                }
            
                val pluginDirectory = File(plugin[1]).also {
                  if(!it.exists()) throw GradleException("""
                    Invalid path for Klutter plugin: '\$it'.
                    Check the .klutter-plugins file in the project root folder.
                  """.trimIndent())
                }
            
                include(plugin[0])
                project(plugin[0]).projectDir = pluginDirectory
            
              }
            }
    '''.replaceAll(" ", "").replaceAll("\n", ""));

  });

  test('Verify content is overwritten if the Gradle file already exists', () {

    pluginLoader.deleteSync();
    pluginLoader.createSync();
    // Write something else then the default generated file content.
    pluginLoader.writeAsStringSync("some nonsense");

    // Run test
    writePluginLoaderGradleFile(root.path);

    // Content should be overwritten
    expect(pluginLoader.readAsStringSync().replaceAll(" ", "").replaceAll("\n", ""),
        ''' // Copyright (c) 2021 - 2022 Buijs Software
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
            import java.io.File
            
            val flutterProjectRoot = rootProject.projectDir.parentFile
            val pluginsFile = File("\$flutterProjectRoot/.klutter-plugins")
            if (pluginsFile.exists()) {
              val plugins = pluginsFile.readLines().forEach { line ->
                val plugin = line.split("=").also {
                  if(it.size != 2) throw GradleException("""
                    Invalid Klutter plugin config.
                    Check the .klutter-plugins file in the project root folder.
                    Required format is: ':klutter:libraryname=local/path/to/flutter/cache/library/artifacts/android'
                  """.trimIndent())
                }
            
                val pluginDirectory = File(plugin[1]).also {
                  if(!it.exists()) throw GradleException("""
                    Invalid path for Klutter plugin: '\$it'.
                    Check the .klutter-plugins file in the project root folder.
                  """.trimIndent())
                }
            
                include(plugin[0])
                project(plugin[0]).projectDir = pluginDirectory
            
              }
            }
    '''.replaceAll(" ", "").replaceAll("\n", ""));

  });

  tearDownAll(() => root.deleteSync(recursive: true));


}