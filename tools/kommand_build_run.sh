./gradlew clean distZip -p "./../lib/kommand"
unzip ./../lib/kommand/build/distributions/kommand.zip -d ./build
./build/kommand/bin/klutter