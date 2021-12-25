import 'GeneratedKlutterAdapter.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Klutter Example',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Klutter'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;
  String platform = '?';

  void _incrementCounter() async {
    platform = await Adapter.getPlatform??"";
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: <Widget>[
            const SizedBox(height: 50),
            const Text('This is an example app build and released with the Klutter Framework. '
                'Klutter is a framework and tool set which uses Flutter to create the frontend'
                'and Kotlin Multiplatform for the backend. Klutter combines industry best practices '
                'for everything from app design to CICD into a single cohesive framework.',
            ),
            const SizedBox(height: 50,),

            Padding(
              padding: const EdgeInsets.all(20),
              child: TextButton(
                onPressed: _incrementCounter,
                child: const Text('Press me!', style:
                TextStyle(color: Colors.white),),
                style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.all(Colors.blue),
                ),
              ),
            ),
            const Text('Pressing this buttons calls a native function. Flutter will '
                'use a predefined platform channel to send a request to the Kotlin Multiplatform '
                'module.'),
            const SizedBox(height: 50),
            Text(
              'Platform: $platform',
              style: Theme.of(context).textTheme.headline5,
            ),
          ],
        ),
      ),
    );
  }
}