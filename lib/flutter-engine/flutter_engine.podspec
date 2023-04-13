Pod::Spec.new do |spec|
    spec.name                     = 'flutter_engine'
    spec.version                  = '0.19.5'
    spec.homepage                 = 'https://buijs.dev'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Klutter - Flutter Engine'
    spec.vendored_frameworks      = 'build/cocoapods/framework/FlutterEngine.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '13'
    spec.dependency 'flutter_framework'
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':lib:flutter-engine',
        'PRODUCT_MODULE_NAME' => 'FlutterEngine',
    }
                
    spec.script_phases = [
        {
            :name => 'Build flutter_engine',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$COCOAPODS_SKIP_KOTLIN_BUILD" ]; then
                  echo "Skipping Gradle build task invocation due to COCOAPODS_SKIP_KOTLIN_BUILD environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end