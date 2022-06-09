package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.Method

class AdapterStub {
    static def get() {
        return new AdapterData(
                new PubspecData(
                        "super_plugin",
                        new PubFlutter(
                                new Plugin(
                                        new Platforms(
                                                new PluginClass("super_plugin", "SuperPlugin"),
                                                new PluginClass("super_plugin", "SuperPlugin"),
                                        )
                                )
                        )
                ),
                [new Method(
                        "greeting",
                        "platform.Greeting",
                        "Greeting().greeting()",
                        false,
                        "String"
                )], [], []
        )
    }
}
