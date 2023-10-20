/* Copyright (c) 2021 - 2023 Buijs Software
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
//file:noinspection GroovyAssignabilityCheck
package dev.buijs.klutter.kradle

import dev.buijs.klutter.kore.project.PubspecBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.nio.file.Files

@Stepwise
class NewProjectWizardSpec extends Specification {

    @Shared
    File folder = Files.createTempDirectory("").toFile()

    def "Verify wizard asks for all required fields"() {
        given:
        def mrWizard = Mock(MrWizard)
        MrWizardKt.mrWizard = mrWizard

        and: "ask for rootFolder"
        mrWizard.promptConfirm("Create project in current folder?", _) >> false
        mrWizard.promptInput("Enter path where to create the project:", _) >> folder.path

        and: "ask for groupName"
        mrWizard.promptInput("Enter Groupname (organisation):", _) >> "foo.boo.yay"

        and: "ask for pluginName"
        mrWizard.promptInput("Enter Plugin name:", _) >> "dummy_plugin"

        and: "ask for flutterPath"
        mrWizard.promptList(
                "press Enter to pick",
                "Select Flutter SDK version:",
                _
        ) >> "3.0.5 (macos ARM64)"

        and: "ask for configOrNull"
        mrWizard.promptConfirm("Configure dependencies?", _) >> false

        when:
        def options = NewProjectWizardKt.toProjectBuilderOptions(new NewProjectWizard())

        then:
        options != null
    }

    def "When specified RootFolder does NOT exist then MrWizard asks again"() {
        given:
        def index = 0
        def folders = ["doesNotExist", folder.path]
        def mrWizard = Mock(MrWizard) {
            2 * it.promptInput("Enter path where to create the project:", _) >> {
                def rootFolder = folders[index]
                index+=1
                rootFolder
            }
        }

        and: "ask for groupName"
        mrWizard.promptInput("Enter Groupname (organisation):", _) >> "foo.boo.yay"

        and: "ask for pluginName"
        mrWizard.promptInput("Enter Plugin name:", _) >> "dummy_plugin"

        and: "ask for flutterPath"
        mrWizard.promptList(
                "press Enter to pick",
                "Select Flutter SDK version:",
                _
        ) >> "3.0.5 (macos ARM64)"

        and: "ask for configOrNull"
        mrWizard.promptConfirm("Configure dependencies?", _) >> false

        and:
        MrWizardKt.mrWizard = mrWizard

        and: "ask for rootFolder"
        mrWizard.promptConfirm("Create project in current folder?", _) >> false

        when:
        def options = NewProjectWizardKt.toProjectBuilderOptions(new NewProjectWizard())

        then:
        options != null
    }

    def "Verify wizard asks for all dependencies"() {
        given:
        def mrWizard = Mock(MrWizard)
        MrWizardKt.mrWizard = mrWizard

        and: "ask for rootFolder"
        mrWizard.promptConfirm("Create project in current folder?", _) >> false
        mrWizard.promptInput("Enter path where to create the project:", _) >> folder.path

        and: "ask for groupName"
        mrWizard.promptInput("Enter Groupname (organisation):", _) >> "foo.boo.yay"

        and: "ask for pluginName"
        mrWizard.promptInput("Enter Plugin name:", _) >> "dummy_plugin"

        and: "ask for flutterPath"
        mrWizard.promptList(
                "press Enter to pick",
                "Select Flutter SDK version:",
                _
        ) >> "3.0.5 (macos ARM64)"

        and: "ask for configOrNull"
        mrWizard.promptConfirm("Configure dependencies?", _) >> true

        and: "ask for squint_json"
        mrWizard.promptList(
                "press Enter to pick",
                "Get squint_json source from:",
                _
        ) >> "Git@Develop"

        and: "ask for klutter_ui"
        mrWizard.promptList(
                "press Enter to pick",
                "Get klutter_ui source from:",
               _
        ) >> "Local"

        and: "ask for klutter_ui local path"
        mrWizard.promptInput("Enter path to klutter_ui (dart) library:", _) >> null

        and: "ask for klutter"
        mrWizard.promptList(
                "press Enter to pick",
                "Get klutter source from:",
               _
        ) >> "Pub@^${PubspecBuilder.klutterPubVersion}"

        and: "ask for bom-version"
        mrWizard.promptInput("Enter bill-of-materials version:", _) >> "9999.x.y.z"

        when:
        def options = NewProjectWizardKt.toProjectBuilderOptions(new NewProjectWizard())

        then:
        options != null
    }
}
