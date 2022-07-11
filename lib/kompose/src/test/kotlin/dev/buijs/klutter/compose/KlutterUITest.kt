package dev.buijs.klutter.compose

import dev.buijs.klutter.ui.*
import dev.buijs.klutter.ui.builder.KlutterScreen
import dev.buijs.klutter.ui.builder.KlutterUI
import dev.buijs.klutter.ui.viewmodel.KomposeViewModel
import dev.buijs.klutter.ui.viewmodel.Navigator
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

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

            val textButton: TextButton<Navigator> = column.children[1] as TextButton<Navigator>
            textButton.text shouldBe "Go to feed"
            val model = textButton.model()
            model.route shouldBe "feed"
            model.arguments shouldBe "Data from home"

        }

        "output valid flutter code" {

            verify(kompose, expected)

        }

    }

})

class UI: KlutterUI(name ="Home") {

    override fun screen(): KlutterScreen = {
        SafeArea {
            Scaffold {
                Center {
                    Column {
                        children(
                            Text("Home"),
                            TextButton(
                                model = Navigator(),
                                text = "Go to feed",
                            ) {
                                route = "feed"
                                arguments = "Data from home"
                              },
                        )
                    }
                }
            }
        }
    }

}

class SomeModel : KomposeViewModel() {

    private var data: String? = null

    fun setData(data: String) {
        this.data = data
    }

}

val expected = """
        |import 'package:flutter_platform_widgets/flutter_platform_widgets.dart';
        |import 'package:flutter/widgets.dart';
        |import 'package:provider/provider.dart';
        |
        |import 'kompose_navigator.dart';
        |
        |class Home extends StatelessWidget {
        |  const Home({Key? key}) : super(key: key);
        |
        |  factory Home.kompose(KomposeRouteArgument? argument) {
        |    return const Home();
        |  }
        |
        |  @override
        |  Widget build(BuildContext context) {
        |    return ChangeNotifierProvider<HomeModel>(
        |      create: (context) => HomeModel(),
        |      child: Consumer<HomeModel>(
        |          builder: (context, model, child) => HomeView(model)),
        |    );
        |  }
        |}
        |
        |class HomeModel extends ChangeNotifier {}
        |
        |class HomeView extends StatelessWidget {
        |  const HomeView(this.model, {Key? key}) : super(key: key);
        |
        |  final HomeModel model;
        |
        |  @override
        |  Widget build(BuildContext context) {
        |    return SafeArea(
        |      child: PlatformScaffold(
        |        body: Center(
        |            child: Column(
        |               children: [
        |                   PlatformText('Home'),
        |                   PlatformTextButton(
        |                       child: Text('Go to feed'),
        |                       onPressed: () => Navigator.pushNamed(
        |                           context, 
        |                           KomposeNavigator.feedRoute,
        |                           arguments: 'Data from home',
        |                       ),
        |                   ),
        |               ]
        |              )
        |          )
        |      )
        |    );
        |  }
        |}
""".trimMargin()