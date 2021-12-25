package dev.buijs.klutter.core.adapter

import java.io.File

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
internal class KlutterIosAdapterWriter {

    fun write(podspec: File) {
        val newPodspecContent = podspec.readLines().asSequence()
//            .map {
//                if (it.contains("spec.name     ")){
//                    "    spec.name                     = '${podspec.name}'"
//                } else it
//            }
//            .map {
//                if (it.contains("spec.vendored_frameworks")) {
//                    "    spec.vendored_frameworks      = \"build/fat-framework/debug/Platform.framework\""
//                } else it
//            }
            .map {
                if (it.contains("'PRODUCT_MODULE_NAME' =>")) {
                    "        'PRODUCT_MODULE_NAME' => 'Platform',"
                } else it
            }
//            .map {
//                if (it.contains("        'KOTLIN_PROJECT_PATH' => ':app-backend:platform',")){
//                    "        'KOTLIN_PROJECT_PATH' => ':platform',"
//                }  else it
//            }
            .map {
                if (it.contains("\"\$REPO_ROOT/../../gradlew\" -p \"\$REPO_ROOT\" \$KOTLIN_PROJECT_PATH:syncFramework \\")) {
                    "                \"\$REPO_ROOT/../gradlew\" -p \"\$REPO_ROOT\" \$KOTLIN_PROJECT_PATH:syncFramework \\"
                } else it
            }
            .map {
                if (it.contains("spec.ios.deployment_target")) {
                    "$it \r\n" +
                            "    spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' } \r\n" +
                            "    spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' } \r\n"
                } else it
            }
            .toList()

        podspec.writeText(newPodspecContent.joinToString(separator = "\n"))
    }



}