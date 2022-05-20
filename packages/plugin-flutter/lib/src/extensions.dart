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
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

import 'dart:io';

import 'klutter_exception.dart';

/// File management utilities.
///
/// [Author] Gillian Buijs.
extension FileUtil on FileSystemEntity {
  /// Execute a fallback function if FileSystemEntity does not exist.
  void ifNotExists(void Function(FileSystemEntity file) doElse) {
    if (!existsSync()) {
      doElse.call(this);
    }
  }

  /// Return FileSystemEntity or execute a fallback function if it does not exist.
  FileSystemEntity orElse(void Function(FileSystemEntity file) doElse) {
    ifNotExists(doElse);
    return this;
  }

  String get absolutePath => absolute.path;
}

/// Utils for easier String manipulation.
///
/// [Author] Gillian Buijs.
extension StringUtil on String {
  /// Create an absolute path to the given folder.
  ///
  /// If the path does not exist throw a [KlutterException].
  String get verifyExists => Directory(this).orElse((folder) {
        throw KlutterException(
          "Path does not exist: ${folder.absolute.path}",
        );
      }).absolutePath;

  /// Utility to print templated Strings.
  ///
  /// Example:
  ///
  /// Given a templated String:
  /// ```
  /// final String foo = """|A multi-line message
  ///                       |is a message
  ///                       |that exists of
  ///                       |multiple lines.
  ///                       |
  ///                       |True story"""";
  /// ```
  ///
  /// Will produce a multi-line String:
  ///
  /// 'A multi-line message
  /// is a message
  /// that exists of
  /// multiple lines.
  ///
  /// True story'
  ///
  /// [Author] Gillian Buijs.
  String get format => replaceAllMapped(
      // Find all '|' char including preceding whitespaces.
      RegExp(r'(\s+?\|)'),
      // Replace them with a single linebreak.
      (_) => "\n").replaceAll("|", "");
}
