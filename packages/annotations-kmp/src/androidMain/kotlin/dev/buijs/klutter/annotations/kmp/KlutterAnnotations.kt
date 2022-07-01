package dev.buijs.klutter.annotations.kmp

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
actual annotation class KlutterAdaptee(
    actual val name: String,
    actual val requiresAndroidContext: Boolean = false,
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
actual annotation class KlutterAdapter()

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
actual annotation class KlutterResponse()

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
actual annotation class AndroidContext()