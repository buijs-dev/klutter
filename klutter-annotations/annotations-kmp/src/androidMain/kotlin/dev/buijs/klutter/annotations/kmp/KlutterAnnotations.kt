package dev.buijs.klutter.annotations.kmp

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
actual annotation class KlutterAdaptee(actual val name: String)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
actual annotation class KlutterAdapter()

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
actual annotation class KlutterResponse()