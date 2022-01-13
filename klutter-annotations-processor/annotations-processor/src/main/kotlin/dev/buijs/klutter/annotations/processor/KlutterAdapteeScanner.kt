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

package dev.buijs.klutter.annotations.processor

import dev.buijs.klutter.core.KotlinFileScanningException
import dev.buijs.klutter.core.MethodCallDefinition
import dev.buijs.klutter.core.MethodData


/**
 * @author Gillian Buijs
 */
class KlutterAdapteeScanner(
    private val fqdn: String?,
    private val className: String,
    private val ktFileBody: String) {

    fun scan(): List<MethodCallDefinition> {

        val methods = mutableListOf<MethodData>()

        val packagename = """package(.*)""".toRegex()
            .find(ktFileBody)
            ?.value
            ?.filter { !it.isWhitespace() }
            ?:""

        val trimmedBody = ktFileBody.filter { !it.isWhitespace() }

        """@KlutterAdaptee\(name="([^"]+?)"\)fun([^:]+?):""".toRegex()
            .findAll(trimmedBody).forEach { match ->
                methods.add(
                    MethodData(
                    getter = match.groups[1]?.value?:throw KotlinFileScanningException("""
                       Unable to process KlutterAdaptee annotation.
                       Please make sure the annotation is as follows: 'klutterAdaptee(name = "foo")'
                       """.trim()),
                    methodCall = match.groups[2]?.value?:throw KotlinFileScanningException("""
                       Unable to process KlutterAdaptee annotation.
                       Please make sure the annotation is as follows: 'klutterAdaptee(name = "foo")'
                       """.trim()),
                    )
                )
            }

        return methods.map {
            MethodCallDefinition(
                call = "$className().${it.methodCall}",
                import = fqdn?:packagename,
                returns = Any::class.java,
                getter = it.getter
            )
        }
    }

}