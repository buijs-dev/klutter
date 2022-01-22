buildscript {

    val file = File("${rootDir.absolutePath}/../publish/_publish.properties").normalize()

    if(!file.exists()) {
        throw GradleException("missing _publish.properties file in ${file.absolutePath}")
    }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

    val user = properties["repo.username"]
        ?:throw GradleException("missing repo.username in _publish.properties")

    val pass = properties["repo.password"]
        ?:throw GradleException("missing repo.password in _publish.properties")

    val endpoint = properties["repo.url"]
        ?:throw GradleException("missing repo.url in _publish.properties")

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

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.0.4")
    }
}

allprojects {
    val file = File("${rootDir.absolutePath}/../publish/_publish.properties").normalize()

    if(!file.exists()) {
        throw GradleException("missing _publish.properties file in ${file.absolutePath}")
    }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

    val user = properties["repo.username"]
        ?:throw GradleException("missing repo.username in _publish.properties")

    val pass = properties["repo.password"]
        ?:throw GradleException("missing repo.password in _publish.properties")

    val endpoint = properties["repo.url"]
        ?:throw GradleException("missing repo.url in _publish.properties")

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

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}