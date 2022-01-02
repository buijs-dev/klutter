package dev.buijs.klutter.core

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class KotlinFileScanningException(msg: String, cause: String = ""): Exception(msg, Throwable(cause))

class KlutterCodeGenerationException(msg: String, cause: String = ""): Exception(msg, Throwable(cause))

class KlutterConfigException(msg: String, cause: String = ""): Exception(msg, Throwable(cause))

class KlutterGradleException(msg: String, cause: String = ""): Exception(msg, Throwable(cause))

class KlutterMultiplatformException(msg: String, cause: String = ""): Exception(msg, Throwable(cause))