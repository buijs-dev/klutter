Pod::Spec.new do |spec|
    spec.name                     = 'flutter_framework'
    spec.version                  = '1.0'
    spec.homepage                 = 'none'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = 'MIT'
    spec.summary                  = 'none'
    spec.vendored_frameworks      = 'Flutter.xcframework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '11.0'
end
