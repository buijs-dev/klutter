include(":app")

//Get local.properties file from android folder or fail
val localPropertiesFile = File(rootProject.projectDir, "local.properties")
if(!localPropertiesFile.exists()){
    throw GradleException("missing local properties")
}

//Load local.properties and search for flutter.sdk or fail
val properties = java.util.Properties()
properties.load(localPropertiesFile.reader())

val flutterSdkPath = properties.getProperty("flutter.sdk")
if(flutterSdkPath == null) {
    throw GradleException("flutter.sdk not set in local.properties")
}

//Apply plugin!
apply(from ="$flutterSdkPath/packages/flutter_tools/gradle/app_plugin_loader.gradle")