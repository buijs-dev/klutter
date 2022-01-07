pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    val file = File("${rootProject.projectDir}/.klutter/klutter.properties").normalize()

    if(!file.exists()) { throw GradleException("File does not exist: ${file.absolutePath}") }

    val properties = HashMap<String, String>()

    file.forEachLine {
        it.split("=").also { pair ->
            if(pair.size == 2){
                properties[pair[0]] = pair[1]
            }
        }
    }

    val user = properties["private.repo.username"]
        ?:throw GradleException("missing private.repo.username in dev.properties")

    val pass = properties["private.repo.password"]
        ?:throw GradleException("missing private.repo.password in dev.properties")

    val endpoint = properties["private.repo.url"]
        ?:throw GradleException("missing private.repo.url in dev.properties")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri(endpoint)
            credentials {
                username = user
                password = pass
            }
        }
    }
}

include(":common")