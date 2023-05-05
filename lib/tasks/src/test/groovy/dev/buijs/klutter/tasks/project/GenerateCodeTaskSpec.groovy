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
package dev.buijs.klutter.tasks.project

import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.ast.CustomType
import dev.buijs.klutter.kore.ast.Method
import dev.buijs.klutter.kore.ast.RequestScopedBroadcastController
import dev.buijs.klutter.kore.ast.RequestScopedController
import dev.buijs.klutter.kore.ast.SingletonBroadcastController
import dev.buijs.klutter.kore.ast.SingletonController
import dev.buijs.klutter.kore.ast.SquintCustomType
import dev.buijs.klutter.kore.ast.SquintCustomTypeMember
import dev.buijs.klutter.kore.ast.SquintMessageSource
import dev.buijs.klutter.kore.ast.StringType
import dev.buijs.klutter.kore.ast.TypeMember
import dev.buijs.klutter.kore.ast.UnitType
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.project.ProjectKt
import dev.buijs.klutter.kore.project.PubspecBuilder
import dev.buijs.klutter.tasks.ExecutorKt
import dev.buijs.klutter.tasks.codegen.GenerateCodeOptions
import dev.buijs.klutter.tasks.codegen.GenerateCodeTaskKt
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class GenerateCodeTaskSpec extends Specification {

    @Shared
    def executor = new Exeggutor()

    @Shared
    def pluginName = "my_awesome_plugin"

    @Shared
    def groupName = "com.example.awesomeness"

    @Shared
    def root = Files.createTempDirectory("").toFile()

    @Shared
    def pathToRoot = root.absolutePath

    @Shared
    def plugin = new File("${pathToRoot}/$pluginName")

    @Shared
    def pathToPlugin = plugin.absolutePath

    @Shared
    def example = new File("${pathToPlugin}/example")

    @Shared
    def pathToExampleIos = "${example.absolutePath}/ios"

    @Shared
    def pathToExample = example.absolutePath

    @Shared
    def sut = new ProjectBuilderTask(
            new ProjectBuilderOptions(
                    Either.ok(new File(pathToRoot)),
                    Either.ok(pluginName),
                    Either.ok(groupName),
                    null))

    @Shared
    def createFlutterPlugin = "flutter create my_awesome_plugin --org com.example.awesomeness --template=plugin --platforms=android,ios -a kotlin -i swift"

    @Shared
    def flutterPubGet = "flutter pub get"

    @Shared
    def klutterProducerInit = "flutter pub run klutter:producer init"

    @Shared
    def klutterConsumerInit = "flutter pub run klutter:consumer init"

    @Shared
    def klutterConsumerAdd = "flutter pub run klutter:consumer add=my_awesome_plugin"

    @Shared
    def iosPodUpdate = "pod update"

    @Shared
    String packageName = "foo.dot.com"

    @Shared
    TypeMember fooType = new TypeMember("foo", new StringType())

    @Shared
    CustomType myCustomType = new CustomType("MyCustomType", packageName, [fooType])

    @Shared
    Method methodReturningMyCustomType = new Method(
            "getFoo",
            "foo.dot.com",
            "foo",
            false,
            myCustomType,
            null,
            null)

    @Shared
    Method methodWithStringRequest = new Method(
            "setFoo",
            "foo.dot.com",
            "foo",
            false,
            new UnitType(),
            new StringType(),
            "data")
    @Shared
    List<Controller> controllers = [
            new RequestScopedBroadcastController(packageName, "MyRequestScopedBroadcaster", [], new StringType()),
            new RequestScopedController(packageName, "MyRequestScoped", [methodReturningMyCustomType]),
            new SingletonController(packageName, "MySingleton", [methodWithStringRequest]),
            new SingletonBroadcastController(packageName, "MySingletonBroadcaster", [], myCustomType)
    ]

    @Shared
    List<SquintMessageSource> messages
    def setupSpec() {
        plugin.mkdirs()
        example.mkdirs()
        ExecutorKt.executor = executor

        def pubspecInRoot = new File("${pathToPlugin}/pubspec.yaml")
        pubspecInRoot.createNewFile()
        pubspecInRoot.write(rootPubspecYaml)

        def pubspecInExample = new File("${pathToExample}/pubspec.yaml")
        pubspecInExample.createNewFile()
        pubspecInExample.write(examplePubspecYaml)

        new File("${pathToPlugin}/android").mkdirs()
        def localProperties = new File("${pathToPlugin}/android/local.properties")
        localProperties.createNewFile()
        localProperties.write("hello=true")
        executor.putExpectation(pathToRoot, createFlutterPlugin)
        executor.putExpectation(pathToPlugin, flutterPubGet)
        executor.putExpectation(pathToExample, flutterPubGet)
        executor.putExpectation(pathToPlugin, klutterProducerInit)
        executor.putExpectation(pathToExample, klutterConsumerInit)
        executor.putExpectation(pathToExample, klutterConsumerAdd)
        executor.putExpectation(pathToExampleIos, iosPodUpdate)
        sut.run()

        def androidMain = plugin.toPath().resolve("android/src/main")
        androidMain.toFile().mkdirs()
        def androidManifest = androidMain.resolve("AndroidManifest.xml")
        androidManifest.toFile().createNewFile()
        def androidSource = androidMain.resolve("kotlin")
        androidSource.toFile().mkdir()

        def ios = plugin.toPath().resolve("ios")
        ios.toFile().mkdirs()
        def iosPodspec = ios.resolve("my_awesome_plugin.podspec")
        iosPodspec.toFile().createNewFile()
        def iosClasses = ios.resolve("Classes")
        iosClasses.toFile().mkdir()
    }

    def "Generate code in new project"() {
        given:
        def myCustomTypeSrcFile = Files.createTempDirectory("")
                .resolve("sqdb_my_custom_type.json")
                .toFile()
        myCustomTypeSrcFile.createNewFile()
        myCustomTypeSrcFile.write("""
            {"className":"${myCustomType.className}","members":[{"name":"foo","type":"String","nullable":false}]}
        """)
        messages = [
                new SquintMessageSource(
                        myCustomType,
                        new SquintCustomType(
                                myCustomType.className,
                                [new SquintCustomTypeMember(fooType.name, fooType.type.className, false)]),
                        myCustomTypeSrcFile)]

        when:
        def rootPubspecYamlFile = Path.of(pathToPlugin).resolve("pubspec.yaml").toFile()
        rootPubspecYamlFile.createNewFile()
        rootPubspecYamlFile.write(rootPubspecYaml)
        def project = ProjectKt.plugin(pathToPlugin)
        def pubspec = PubspecBuilder.toPubspec(rootPubspecYamlFile)
        def srcFolder = project.root.pathToLibFolder.toPath().resolve("src")

        and:
        executor.putExpectation(pathToPlugin, "flutter pub get")
        executor.putExpectation(project.root.pathToLibFolder.absolutePath, "dart format .")
        executor.putExpectationWithAction(pathToPlugin, "flutter pub run squint_json:generate" +
                                    " --type dataclass" +
                                    " --input ${myCustomTypeSrcFile.path}" +
                                    " --output ${srcFolder.toFile().path}" +
                                    " --overwrite true" +
                                    " --generateChildClasses false" +
                                    " --includeCustomTypeImports true",
                { srcFolder.resolve("my_custom_type_dataclass.dart").toFile().createNewFile() })
        executor.putExpectation(pathToPlugin, "flutter pub run squint_json:generate" +
                                        " --type serializer" +
                                        " --input ${srcFolder.resolve("my_custom_type_dataclass.dart").toFile().path}" +
                                        " --output ${srcFolder.toFile().path}" +
                                        " --overwrite true")

        and:
        GenerateCodeTaskKt.toGenerateCodeTask(new GenerateCodeOptions(
                project, pubspec, false, controllers, messages, { println("$it") })).run()

        then:
        def flutterLib = project.root.pathToLibFile
        flutterLib.exists()
        flutterLib.text.contains("export 'src/my_custom_type_dataclass.dart';")
        flutterLib.text.contains("export 'src/my_request_scoped_broadcaster/controller.dart'")
        flutterLib.text.contains("export 'src/my_singleton/foo.dart'")
        flutterLib.text.contains("export 'src/my_singleton_broadcaster/controller.dart'")
        flutterLib.text.contains("export 'src/my_request_scoped/foo.dart'")

        and:
        def myRequestScopedControllerDartSource = srcFolder.resolve("my_request_scoped")
        myRequestScopedControllerDartSource.toFile().exists()

        def myRequestScopedBroadcastControllerDartSource = srcFolder.resolve("my_request_scoped_broadcaster")
        myRequestScopedBroadcastControllerDartSource.toFile().exists()

        def mySingletonControllerDartSource = srcFolder.resolve("my_singleton")
        mySingletonControllerDartSource.toFile().exists()

        def mySingletonBroadcastControllerDartSource = srcFolder.resolve("my_singleton_broadcaster")
        mySingletonBroadcastControllerDartSource.toFile().exists()
    }

    @Shared
    def rootPubspecYaml = """name: my_awesome_plugin
description: A new klutter plugin project.
version: 0.0.1

environment:
  sdk: '>=2.16.1 <3.0.0'
  flutter: ">=2.5.0"

dependencies:
    flutter:
        sdk: flutter

    squint_json: ^0.0.6
    klutter_ui: ^0.0.2
dev_dependencies:
    klutter: ^0.3.0
flutter:
  plugin:
    platforms:
      android:
        package: null
        pluginClass: null
      ios:
        pluginClass: null
            """

    @Shared
    def examplePubspecYaml = """name: my_awesome_plugin_example
description: Demonstrates how to use the my_awesome_plugin plugin
publish_to: 'none' # Remove this line if you wish to publish to pub.dev

environment:
  sdk: '>=2.16.1 <3.0.0'

dependencies:
    flutter:
        sdk: flutter

    my_awesome_plugin:
        path: ../

    klutter_ui: ^0.0.2
    squint_json: ^0.0.6
dev_dependencies:
    flutter_test:
        sdk: flutter

    klutter: ^0.3.0
flutter:
    uses-material-design: true
"""
}
