package dev.buijs.klutter.kompose

data class Bindings(
    val channels: List<String>,
    val streams: List<String>,
)

fun loadBindings() = Bindings(
    channels = listOf(
        "com.example.awesome.my_awesome_app"
    ),
    streams = listOf(
        "com.example.awesome.my_awesome_app/stream/greeting"
    )
)