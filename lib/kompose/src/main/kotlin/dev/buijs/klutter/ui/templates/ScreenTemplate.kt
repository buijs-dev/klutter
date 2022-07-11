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

package dev.buijs.klutter.ui.templates

import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.ui.KomposeBody

/**
 *
 */
class ScreenTemplate(
    /**
     * Name of generated Flutter class.
     */
    private val name: String,

    /**
     * Data to generate Flutter class.
     */
    private val view: KomposeBody,
): KlutterPrinter {

    override fun print(): String {
        return """
            |import 'package:flutter_platform_widgets/flutter_platform_widgets.dart';
            |import 'package:flutter/widgets.dart';
            |import 'package:provider/provider.dart';
            |
            |import 'kompose_navigator.dart';
            |
            |class $name extends StatelessWidget {
            |  const $name({Key? key}) : super(key: key);
            |
            |  factory $name.kompose(KomposeRouteArgument? argument) {
            |    return const $name();
            |  }
            |
            |  @override
            |  Widget build(BuildContext context) {
            |    return ChangeNotifierProvider<${name}Model>(
            |      create: (context) => ${name}Model(),
            |      child: Consumer<${name}Model>(
            |          builder: (context, model, child) => ${name}View(model)),
            |    );
            |  }
            |}
            |
            |class ${name}Model extends ChangeNotifier {}
            |
            |class ${name}View extends StatelessWidget {
            |  const ${name}View(this.model, {Key? key}) : super(key: key);
            |
            |  final ${name}Model model;
            |
            |  @override
            |  Widget build(BuildContext context) {
            |    return ${view.print()};
            |  }
            |}
            |""".trimMargin()
    }

}