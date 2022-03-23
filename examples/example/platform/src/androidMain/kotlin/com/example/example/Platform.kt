package com.example.example

actual class Platform actual constructor() {
    actual val platform: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class Platform() {
    val platform: String
}

actual class Platform actual constructor() {
    actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}