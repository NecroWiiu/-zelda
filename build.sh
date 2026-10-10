#!/usr/bin/env bash
# Linux/macOS: necesitas JDK 17+ y Gradle 8.6+ instalados (o ejecuta "gradle wrapper" una vez).
set -e
cd "$(dirname "$0")"
gradle build
mkdir -p dist && cp build/libs/hyrulecraft-1.0.0.jar dist/
echo "Listo: dist/hyrulecraft-1.0.0.jar"
