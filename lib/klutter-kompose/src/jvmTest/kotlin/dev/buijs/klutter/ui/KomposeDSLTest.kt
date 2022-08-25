package dev.buijs.klutter.ui

import dev.buijs.klutter.annotations.KlutterJSON
import dev.buijs.klutter.annotations.KomposeView
import dev.buijs.klutter.ui.builder.JetlagScreen
import dev.buijs.klutter.ui.builder.JetlagUI
import dev.buijs.klutter.ui.builder.KlutterScreen
import dev.buijs.klutter.ui.builder.KlutterUI
import dev.buijs.klutter.ui.controller.KomposeController
import dev.buijs.klutter.ui.event.KlutterEvent
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy

internal class KomposeDSLTest : WordSpec({

    "Using the KlutterUI DSL" should {

        val kompose = UI()

        "return all kompose data in dto's" {

            kompose.view().hasChild() shouldBe true

            kompose.name() shouldBe "Home"
            val safeArea: KomposeSafeArea = kompose.view().child() as KomposeSafeArea
            val scaffold: KomposeScaffold = safeArea.child as KomposeScaffold
            val center: KomposeCenter = scaffold.child as KomposeCenter

            val column: KomposeColumn = center.child as KomposeColumn

            val text: Text = column.children.first() as Text
            text.data shouldBe "Home"

            // TODO fix
            //            val textButton: TextButton<NavigationController> = column.children[1] as TextButton<NavigationController>
            //            textButton.text shouldBe "Go to feed"
            //            val model = textButton.model()
            //            model.route shouldBe "feed"
            //            model.arguments shouldBe "Data from home"

        }

        "output valid flutter code" {

            verify(kompose, expected)

        }


        "test" {

            val field = TextField(
                event = GreetingIncrementCount::class,
                hint = "blabla",
            )

            field.eventType shouldBe "dev.buijs.klutter.ui.GreetingIncrementCount"
            field.controllerType shouldBe "dev.buijs.klutter.ui.GreetingController"
            field.stateType() shouldBe "dev.buijs.klutter.ui.Greeting"

            field.print() shouldBe "PlatformTextField(\n" +
                    "   hintText: \"blabla\",\n" +
                    "   onChanged: (text) => KomposeAppBackend.fireEvent(\n" +
                    "      controller: \"dev.buijs.klutter.ui.GreetingController\",\n" +
                    "      widget: \"textfield\",\n" +
                    "      event: \"dev.buijs.klutter.ui.GreetingIncrementCount\",\n" +
                    "      data: text,\n" +
                    "    ).then(_updateGreetingState)\n" +
                    ")"

        }

    }
})

class GreetingIncrementCount: KlutterEvent<GreetingIncrementCount, GreetingController>()

class GreetingController : KomposeController<Greeting>(state = Greeting()) {
    override fun onEvent(event: String, data: Any?) {
        TODO("Not yet implemented")
    }
}

@Serializable
class Greeting: KlutterJSON<Greeting>() {
    override fun data(): Greeting {
        TODO("Not yet implemented")
    }

    override fun strategy(): SerializationStrategy<Greeting> {
        TODO("Not yet implemented")
    }
}

@KomposeView
class Home : JetlagUI(name = "Home") {

    override fun screen(): JetlagScreen = {
        Scaffold {
            child = TextField(
                event = GreetingIncrementCount::class,
                hint = "enter new greeting",
            )
        }

    }
}


class UI : KlutterUI(name = "Home") {

    override fun screen(): KlutterScreen = {
        SafeArea {
            Scaffold {
                Center {
                    Column {
                        children(
                            Text("Home"),
                            TextField(
                                event = GreetingIncrementCount::class,
                                hint = "enter new greeting",
                            )
                        )
                    }
                }
            }
        }
    }

}

val expected = """
        |// Copyright (c) 2021 - 2022 Buijs Software
        |//
        |// Permission is hereby granted, free of charge, to any person obtaining a copy
        |// of this software and associated documentation files (the "Software"), to deal
        |// in the Software without restriction, including without limitation the rights
        |// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        |// copies of the Software, and to permit persons to whom the Software is
        |// furnished to do so, subject to the following conditions:
        |//
        |// The above copyright notice and this permission notice shall be included in all
        |// copies or substantial portions of the Software.
        |//
        |// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        |// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        |// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        |// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        |// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        |// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        |// SOFTWARE.
        |import "dart:convert";
        |import "package:flutter/widgets.dart";
        |import "package:flutter_platform_widgets/flutter_platform_widgets.dart";
        |import "package:kompose_app_backend/kompose_app_backend.dart";
        |
        |import "kompose_navigator.dart";
        |
        |/// Autogenerated by Klutter Framework with Kompose.
        |///
        |/// PUB docs: https://pub.dev/packages/klutter
        |/// 
        |/// Generated UI class for screen named Home.
        |class Home extends StatelessWidget {
        |
        |  /// Construct a new Home instance.
        |  const Home({Key? key}) : super(key: key);
        |
        |  /// Construct a new Home instance and process
        |  /// any data received from the invoking class.
        |  factory Home.kompose(KomposeRouteArgument? argument) {
        |    return const Home();
        |  }
        |
        |  @override
        |  Widget build(BuildContext context) {
        |    return const _HomeView();
        |  }
        |}
        |
        |class _HomeView extends StatefulWidget {
        |  const _HomeView({Key? key}) : super(key: key);
        |
        |  @override
        |  State<_HomeView> createState() => _HomeViewState();
        |}
        |
        |class _HomeViewState extends State<_HomeView> {
        |
        |  /// State of Greeting which is updated everytime
        |  /// an event is processed by the backend.
        |  Greeting? _greetingState;
        |
        |void _updateGreetingState(dynamic state) {
        |  setState((){
        |    if(state == null) {
        |        _greetingState = null;
        |      } else {
        |        final dynamic json = jsonDecode(state as String);
        |        _greetingState = Greeting.fromJson(json);
        |      }
        |    });
        |  }
        |
        |
        |  @override
        |  void initState() {
        |    super.initState();
        |    KomposeAppBackend.initController(
        |    widget: "", 
        |    event: "init", 
        |    data: "", 
        |    controller: "dev.buijs.klutter.compose.GreetingController",
        |).then(_updateGreetingControllerState);
        |  }
        |
        |  @override
        |  void dispose() {
        |    KomposeAppBackend.disposeController(
        | widget: "",
        |  event: "init",
        |  data: "",
        |  controller: "dev.buijs.klutter.compose.GreetingController",
        |);  
        |    super.dispose();
        |  }
        |
        |  @override
        |  Widget build(BuildContext context) {
        |    return SafeArea(
        |   child: PlatformScaffold(
        |   body: Center(
        |   child: Column(
        |   children: [
        |   PlatformText('Home'),
        |PlatformTextField(
        |   hintText: "enter new greeting",
        |   onChanged: (text) => KomposeAppBackend.fireEvent(
        |      controller: "dev.buijs.klutter.compose.GreetingController",
        |      widget: "textfield",
        |      event: "dev.buijs.klutter.compose.GreetingIncrementCount",
        |      data: text,
        |    ).then(_updateGreetingState)
        |),
        |   ]
        |)
        |)
        |)
        |);
        |  }
        |}
        |
        |""".trimMargin()