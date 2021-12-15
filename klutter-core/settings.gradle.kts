dependencyResolutionManagement {

    val file = File("${rootDir.absolutePath}/dev.properties").normalize()

    if(!file.exists()) {
        throw GradleException("missing dev.properties file in ${file.absolutePath}")
    }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

    val repsyUsername = properties["repsy.username"]
        ?:throw GradleException("missing repsy.username in dev.properties")

    val repsyPassword = properties["repsy.password"]
        ?:throw GradleException("missing repsy.password in dev.properties")

    val repsyUrl = properties["repsy.url"]
        ?:throw GradleException("missing repsy.url in dev.properties")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri(repsyUrl)
            credentials {
                username = repsyUsername
                password = repsyPassword
            }
        }
    }
}

rootProject.name = "klutter-core"
include("lib")