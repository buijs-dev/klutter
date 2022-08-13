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

package dev.buijs.klutter.kore.shared

import java.io.File

/**
 * Read the content of a Kotlin Class File and return the classes found as String.
 */
internal fun File.toClassBodies(): List<String> {

    // The class content to be returned.
    val output = mutableListOf<String>()

    val lines = readLines()

    // Indicates if the line being processed is
    // preceded by as 'class XXX {' declaration.
    var isInClass = false

    // Counter to keep track of the amount of brackets
    // encountered while processing the content of a Class.
    //
    // Counter starts when [isInClass] is true.
    // Counter is reset when [isInClass is false.
    var bracketCount = 0

    // The current linenumber
    var currentLine = 0

    // Keep track of the linenumber where a Class starts.
    var openingLine = 0

    for(line in lines) {

        currentLine += 1

        // Keep track of opening and closing brackets
        // to determine the end of a class declaration.
        if(isInClass) {
            bracketCount += line.countBrackets()
        }

        // When opening and closing bracket count are equal
        // then the entire class body has been processed
        // and should be stored.
        if(isInClass && bracketCount == 0) {
            isInClass = false
            output.add(lines.join(openingLine, currentLine))
        }

        // Determine if the line is the start of
        // a new class declaration.
        if(line.isClassDeclaration()) {
            isInClass = true
            openingLine = currentLine - 1
            bracketCount += line.countBrackets()
        }

    }

    return output

}

/**
 * Join a sublist of lines to a String.
 */
private fun List<String>.join(from: Int, to: Int): String {
    val toBeJoined = mutableListOf<String>()

    var i = from

    while(i < to) { toBeJoined.add(this[i]); i++ }

    return toBeJoined.joinToString("\n") { it }
}

/**
 * Check if the current line declares a new Class.
 */
private fun String.isClassDeclaration(): Boolean {
    return when {

        startsWith("class ") -> true

        contains(" class ") -> true

        else -> false

    }
}

/**
 * Count the opening and closing brackets and returns the difference.
 */
private fun String.countBrackets(): Int {

    var bracketCount = 0

    for(char in toCharArray()) {

        if(char == '{') {
            bracketCount += 1
        }

        if(char == '}') {
            bracketCount -= 1
        }

    }

    return bracketCount
}