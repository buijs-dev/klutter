package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.IOS
import dev.buijs.klutter.core.MethodData
import dev.buijs.klutter.core.Root
import dev.buijs.klutter.core.test.TestProject
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec


/**
 * @author Gillian Buijs
 */
internal class IosAppDelegateGeneratorTest: WordSpec({

    "Using the AppFrameworkInfoPlistVisitor" should {

        val klutterProject = TestProject()

        IosAppDelegateGenerator(
            methods = listOf(
                MethodData(
                    getter = "hasUpdate",
                    import = "blabla",
                    call = "MonsterdexService().hasUpdate()",
                    returns = "Boolean",
                    async = false
                ),
                MethodData(
                    getter = "getMonsters",
                    import = "blabla",
                    call = "MonsterdexService().getMonsters()",
                    returns = "MonstersResponse",
                    async = true
                ),
            ),
            ios = IOS(root = Root(klutterProject.projectDir.toFile())),
            podName = "common",
        ).generate()

        val generated = klutterProject.iosRunnerDir.resolve("AppDelegate.swift")
        generated.exists() shouldBe true

        generated.readText().filter { !it.isWhitespace() } shouldBe  """
               //File generated by Klutter Framework.
                //Do not edit directly!
                
                import UIKit
                import Flutter
                import common
                
                @UIApplicationMain
                @objc class AppDelegate: FlutterAppDelegate {
                  override func application(
                    _ application: UIApplication,
                    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
                  ) -> Bool {
                  
                    GeneratedPluginRegistrant.register(with: self)
                
                    let channelName = "KLUTTER"
                    let rootViewController = window?.rootViewController as! FlutterBinaryMessenger
                    let methodChannel = FlutterMethodChannel(name: channelName, binaryMessenger: rootViewController)
                
                    methodChannel.setMethodCallHandler {(call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
                    
                        switch call.method {
                        case "hasUpdate":
                            self.hasUpdate(result: result)
                        case "getMonsters":
                            self.getMonsters(result: result)
                        default:
                            result(FlutterMethodNotImplemented)
                        }
                
                    }
                    
                    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
                    
                  }
                  
                    func hasUpdate(result: @escaping FlutterResult) {
                        result(MonsterdexService().hasUpdate())
                    }
                
                    func getMonsters(result: @escaping FlutterResult) {
                        MonsterdexService().getMonsters { data, error in
                
                            if let response = data { result(response.toKJson()) }
                
                            if let failure = error { result(failure) }
                
                        }
                    }
                  
                
                }
            """.trimIndent().filter { !it.isWhitespace() }

        }

})