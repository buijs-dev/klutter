cd ..
cd "klutter-core" || echo "klutter-core not found"
./gradlew koverReport

cd ..
cd "klutter-plugins" || echo "klutter-plugins not found"
./gradlew koverReport