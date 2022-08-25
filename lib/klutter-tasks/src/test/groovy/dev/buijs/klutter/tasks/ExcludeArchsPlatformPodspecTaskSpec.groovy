package dev.buijs.klutter.tasks


import dev.buijs.klutter.kore.project.ProjectKt
import dev.buijs.klutter.kore.test.TestPlugin
import spock.lang.Specification

class ExcludeArchsPlatformPodspecTaskSpec extends Specification {

    def "Verify excluded lines are not duplicated"(){

        given:
        def plugin = new TestPlugin()
        plugin.platformPodSpec.write(podfile)
        def project = ProjectKt.plugin(plugin.root)

        when:
        new ExcludeArchsPlatformPodspecTask(project).run()

        then:
        project.platform.podspec().text.replaceAll(" ", "") == expected.replaceAll(" ", "")

        where:
        podfile = """
                    Pod::Spec.new do |spec|
                    
                    spec.ios.deployment_target = '14.1'
                    spec.pod_target_xcconfig = {
                        'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                        'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
                    }       
                """
        expected = """
            Pod::Spec.new do |spec|
            
            spec.ios.deployment_target = '14.1'
            spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
            spec.pod_target_xcconfig = {
                'KOTLIN_PROJECT_PATH' => ':klutter:ridiculous_plugin',
                'PRODUCT_MODULE_NAME' => 'ridiculous_plugin',
            }       
        """

    }

}
