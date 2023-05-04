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
package dev.buijs.klutter.tasks

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
import dev.buijs.klutter.kore.project.Config
import dev.buijs.klutter.kore.project.Dependencies
import dev.buijs.klutter.kore.project.Project
import dev.buijs.klutter.kore.project.ProjectKt
import dev.buijs.klutter.kore.project.Pubspec
import dev.buijs.klutter.kore.project.PubspecBuilder
import dev.buijs.klutter.tasks.codegen.GenerateCodeOptions
import dev.buijs.klutter.tasks.codegen.GenerateCodeTask
import dev.buijs.klutter.tasks.codegen.GenerateCodeTaskKt
import dev.buijs.klutter.tasks.project.InputGroupNameKt
import dev.buijs.klutter.tasks.project.InputPluginNameKt
import dev.buijs.klutter.tasks.project.InputRootFolderKt
import dev.buijs.klutter.tasks.project.ProjectBuilderOptions
import dev.buijs.klutter.tasks.project.ProjectBuilderTask
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.nio.file.Files

@Stepwise
class NewProjectSystemSpec extends Specification {

    @Shared
    File root

    @Shared
    Project project

    @Shared
    Pubspec pubspec

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
        root = Files.createTempDirectory("").toFile()
        root.toPath().resolve("my_custom_plugin").toFile().mkdir()
    }

    def "Create a new project using local Tasks and pub dependencies from GH @develop"() {
        when:
        new ProjectBuilderTask(
                new ProjectBuilderOptions(
                        InputRootFolderKt.toRootFolder(root.absolutePath),
                        InputPluginNameKt.toPluginName("my_custom_plugin"),
                        InputGroupNameKt.toGroupName("com.example.apps"),
                        new Config(new Dependencies(
                                "https://github.com/buijs-dev/klutter-dart.git@develop",
                                "https://github.com/buijs-dev/klutter-dart-ui.git@develop",
                                "https://github.com/buijs-dev/squint.git@develop", Set.of()),
                                PubspecBuilder.klutterKommanderVersion))).run()

        then:
        def rootFolder = root.toPath().resolve("my_custom_plugin").toFile()
        def plugin = ProjectKt.plugin(rootFolder)

        when:
        project = plugin
        pubspec = PubspecBuilder.toPubspec(plugin.root.resolve("pubspec.yaml"))

        then:
        project.root.folder.exists()
        project.root.pathToLibFolder.exists()
        project.root.pluginClassName == "MyCustomPlugin"
        project.root.pluginName == "my_custom_plugin"

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
        GenerateCodeTaskKt.toGenerateCodeTask(new GenerateCodeOptions(
                project, pubspec, true, controllers, messages, { println("$it") })).run()

        then:
        project.root.pathToLibFile.exists()
        project.root.pathToLibFile.text.contains("my_custom_type") // exports the generated dart data class

        and:
        def srcFolder = project.root.pathToLibFolder.toPath().resolve("src")
        def myRequestScopedControllerDartSource = srcFolder.resolve("my_request_scoped")
        myRequestScopedControllerDartSource.toFile().exists()

        def myRequestScopedBroadcastControllerDartSource = srcFolder.resolve("my_request_scoped_broadcaster")
        myRequestScopedBroadcastControllerDartSource.toFile().exists()

        def mySingletonControllerDartSource = srcFolder.resolve("my_singleton")
        mySingletonControllerDartSource.toFile().exists()

        def mySingletonBroadcastControllerDartSource = srcFolder.resolve("my_singleton_broadcaster")
        mySingletonBroadcastControllerDartSource.toFile().exists()

    }



}