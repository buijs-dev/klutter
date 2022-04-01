package dev.buijs.klutter.core.tasks.adapter.flutter

import dev.buijs.klutter.core.tasks.adapter.platform.IosPodspecVisitor
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files


class IosAdapterVisitorTest : WordSpec({

    "Using the IosAdapterVisitor" should {
        "Edit the podspec" {
            val project = Files.createTempDirectory("")
            val podspec = project.resolve("common.podspec").toFile()

            podspec.writeText("""
                 Pod::Spec.new do |spec|
                spec.name                     = 'common'
                spec.version                  = '0.2.20'
                spec.homepage                 = 'Link to the Shared Module homepage'
                spec.source                   = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
                spec.authors                  = ''
                spec.license                  = ''
                spec.summary                  = 'Some description for the Shared Module'
            
                spec.vendored_frameworks      = "build/cocoapods/framework/common.framework"
                spec.libraries                = "c++"
                spec.module_name              = "#{spec.name}_umbrella"
            
                spec.ios.deployment_target = '13.0'
            
                spec.pod_target_xcconfig = {
                    'KOTLIN_PROJECT_PATH' => ':common',
                    'PRODUCT_MODULE_NAME' => 'common',
                }
                                    
                spec.script_phases = [ { } ]
                end
            """.trimIndent())

            IosPodspecVisitor(podspec).visit()

            podspec.readText().filter { !it.isWhitespace() } shouldBe """
                Pod::Spec.new do |spec|
                spec.name                     = 'common'
                spec.version                  = '0.2.20'
                spec.homepage                 = 'Link to the Shared Module homepage'
                spec.source                   = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
                spec.authors                  = ''
                spec.license                  = ''
                spec.summary                  = 'Some description for the Shared Module'
            
                spec.vendored_frameworks      = "build/fat-framework/debug/common.framework"
                spec.libraries                = "c++"
                spec.module_name              = "#{spec.name}_umbrella"
            
                spec.ios.deployment_target = '13.0'
            
                #These lines are added by the Klutter Framework to enable the app to run on a simulator
                spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
                spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            
                spec.pod_target_xcconfig = {
                    'KOTLIN_PROJECT_PATH' => ':common',
                    'PRODUCT_MODULE_NAME' => 'common',
                }
            
                spec.script_phases = [ { } ]
                end
                """.filter { !it.isWhitespace() }
        }
    }
})