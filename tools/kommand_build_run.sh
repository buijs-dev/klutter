./gradlew clean distZip -p "./../lib/kradle"
unzip ./../lib/kradle/build/distributions/kradle.zip -d ./build
./build/kradle/bin/kradlew