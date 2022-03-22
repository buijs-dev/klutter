val properties = mutableMapOf<String, String>().also { map ->

    App::class.java.getResourceAsStream("/buildsrc.properties")
        ?.bufferedReader()
        ?.forEachLine {
            val pair = it.split("=")
            if(pair.size == 2){
                map[pair[0]] = pair[1]
            }
        } ?: throw BuildSrcException("buildsrc.properties not found")

}

object App {
    val versionCode = properties["app.version.code"] ?: throw BuildSrcException("Missing property app.version.code")
    val versionName = properties["app.version.name"] ?: throw BuildSrcException("Missing property app.version.name")
    val applicationId = properties["app.id"] ?: throw BuildSrcException("Missing property app.id")
}

object Android {
    val compileSdk = properties["android.sdk.compile"] ?: throw BuildSrcException("Missing property android.compile.sdk")
    val minSdk = properties["android.sdk.min"] ?: throw BuildSrcException("Missing property android.min.sdk")
    val targetSdk = properties["android.sdk.target"] ?: throw BuildSrcException("Missing property android.target.sdk")
}

object Ios {
    val version = properties["ios"] ?: throw BuildSrcException("Missing property ios")
}

object Libraries {
    val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    val androidGradle = "com.android.tools.build:gradle:${Versions.androidGradle}"
    val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val klutterAnnotationsKmp = "dev.buijs.klutter:annotations-kmp:${Versions.klutter}"

    const val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationJson}"
}

object Versions {
    val gradle = properties["gradle"] ?: throw BuildSrcException("Missing property gradle")
    val kotlin = properties["kotlin"] ?: throw BuildSrcException("Missing property kotlin")
    val androidGradle = properties["android.gradle"] ?: throw BuildSrcException("Missing property android.gradle")
    val klutter = properties["klutter"] ?: throw BuildSrcException("Missing property klutter")

    const val kotlinxSerializationJson = "1.3.2"

    //Test
    const val junit = "4.13.2"
}