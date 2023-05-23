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

import dev.buijs.klutter.kore.KlutterException
import spock.lang.Specification

import java.nio.file.Files

class CopyXCFrameworkTaskSpec extends Specification {

    def "For plugin copies Platform.xcframework from platform to root/ios folder"() {
        given:
        def root = Files.createTempDirectory("")
                .toAbsolutePath()
                .toFile()

        // root/app/backend/ios/Klutter exists
        // root/app/backend/ios/Klutter/Platform.xcframework does not exist
        def target = new File("${root.absolutePath}/ios/Klutter")
        target.mkdirs()

        // root/platform/build/XCFrameworks/release/Platform.xcframework exists
        def xcframework = new File("${root.absolutePath}/platform/build/XCFrameworks/release/Platform.xcframework")
        xcframework.mkdirs()

        def plist = new File("${xcframework.absolutePath}/Info.plist")
        plist.createNewFile()
        plist.write("TC2")

        when:
        new CopyXCFrameworkTask(root).run()

        then:
        with(target.absolutePath) {
            new File("${it}/Platform.xcframework").exists()
            new File("${it}/Platform.xcframework/Info.plist").exists()
            new File("${it}/Platform.xcframework/Info.plist").text == "TC2"
        }

    }

    def "If ios/Klutter folder does not exist then an exception is thrown"() {
        given:
        def root = Files.createTempDirectory("")
                .toAbsolutePath()
                .toFile()

        when:
        new CopyXCFrameworkTask(root).run()

        then:
        KlutterException e = thrown()
        e.message.startsWith("Path does not exist:")
        e.message.endsWith("/ios/Klutter")
    }

}