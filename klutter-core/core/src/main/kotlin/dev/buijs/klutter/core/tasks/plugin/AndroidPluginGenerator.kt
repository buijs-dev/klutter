package dev.buijs.klutter.core.tasks.plugin

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import dev.buijs.klutter.core.tasks.shared.DefaultWriter
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class AndroidPluginGenerator(
    private val path: File,
    private val methodChannelName: String,
    private val libraryConfig: FlutterLibraryConfig,
    private val methods: List<MethodCallDefinition>,
): KlutterFileGenerator() {

    override fun printer() = AndroidPluginPrinter(
        libraryConfig = libraryConfig,
        methodChannelName = methodChannelName,
        methods = methods,
    )

    override fun writer() = DefaultWriter(path, printer().print())

}

internal class AndroidPluginPrinter(
    private val libraryConfig: FlutterLibraryConfig,
    private val methodChannelName: String,
    private val methods: List<MethodCallDefinition>,
): KlutterPrinter {

    override fun print(): String {

        val block = if (methods.isEmpty()) {
            "return result.notImplemented()"
        } else {
            val defs = methods.joinToString("\n") { printFun(it) }
            "$defs \n                else -> result.notImplemented()"
        }

        val imports = methods
            .map { it.import }
            .distinct()
            .joinToString("\n") { "import $it" }

        return """
            |package ${libraryConfig.developerOrganisation}.${libraryConfig.libraryName}
            |
            |$imports
            |import androidx.annotation.NonNull
            |
            |import io.flutter.embedding.engine.plugins.FlutterPlugin
            |import io.flutter.plugin.common.MethodCall
            |import io.flutter.plugin.common.MethodChannel
            |import io.flutter.plugin.common.MethodChannel.MethodCallHandler
            |import io.flutter.plugin.common.MethodChannel.Result
            |import kotlinx.coroutines.CoroutineScope
            |import kotlinx.coroutines.Dispatchers
            |import kotlinx.coroutines.launch
            |
            |/** ${libraryConfig.pluginClassName} */
            |class ${libraryConfig.pluginClassName}: FlutterPlugin, MethodCallHandler {
            |  /// The MethodChannel that will the communication between Flutter and native Android
            |  ///
            |  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
            |  /// when the Flutter Engine is detached from the Activity
            |  private lateinit var channel : MethodChannel
            |   
            |  private val mainScope = CoroutineScope(Dispatchers.Main) 
            |   
            |  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
            |    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "$methodChannelName")
            |    channel.setMethodCallHandler(this)
            |  }
            |
            |  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
            |        mainScope.launch {
            |           when (call.method) {
            |$block
            |           }
            |        }
            |  }
            |
            |  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
            |    channel.setMethodCallHandler(null)
            |  }
            |}
            |""".trimMargin()
    }

    private fun printFun(definition: MethodCallDefinition): String {
        val type = if (DartKotlinMap.toMapOrNull(definition.returns) == null) {
            ".toKJson()"
        } else ""

        return  "                \"${definition.getter}\" -> {\r\n" +
                "                    result.success(${definition.call}${type})\r\n" +
                "                }"
    }

}