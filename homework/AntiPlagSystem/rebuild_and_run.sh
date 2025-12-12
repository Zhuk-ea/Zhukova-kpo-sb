#!/bin/bash

# ==============================================
# Script: rebuild_and_run.sh
# Описание: Полная пересборка и запуск AntiPlagSystem
# Использование: ./rebuild_and_run.sh
# ==============================================

set -e  # Останавливаем скрипт при ошибке

echo "🚀 Запуск полной пересборки AntiPlagSystem"
echo "=========================================="

# 1. Останавливаем и удаляем всё связанное с проектом
echo "📦 Шаг 1: Остановка и очистка Docker..."
if docker-compose ps -q > /dev/null 2>&1; then
    echo "   Останавливаем контейнеры..."
    docker-compose down --rmi all --volumes --remove-orphans
else
    echo "   Контейнеры не запущены, пропускаем..."
fi

# 2. Удаляем образы проекта
echo "🗑️  Шаг 2: Удаление образов проекта..."
if docker images | grep -q "antiplagsystem"; then
    echo "   Удаляем образы antiplagsystem..."
    docker images | grep "antiplagsystem" | awk '{print $3}' | xargs -r docker rmi -f
else
    echo "   Образы antiplagsystem не найдены..."
fi

# 3. Очистка Docker
echo "🧹 Шаг 3: Очистка Docker системы..."
echo "   Удаляем dangling образы..."
docker image prune -f
echo "   Общая очистка системы..."
docker system prune -f

# 4. Проверка Gradle
echo "📋 Шаг 4: Проверка Gradle..."
if ! command -v ./gradlew &> /dev/null; then
    echo "❌ Ошибка: gradlew не найден!"
    exit 1
fi

# 5. Пересборка проекта
echo "🔨 Шаг 5: Пересборка проекта..."
echo "   Очистка предыдущих сборок..."
./gradlew clean

echo "   Сборка проекта..."
./gradlew build

# Проверка что JAR файлы созданы
echo "✅ Проверка созданных JAR файлов:"
for service in api-gateway file-storing-service file-analysis-service; do
    jar_file="$service/build/libs/*.jar"
    if ls $jar_file 1> /dev/null 2>&1; then
        echo "   ✓ $service: OK ($(ls -sh $jar_file | cut -d' ' -f1))"
    else
        echo "   ✗ $service: JAR не найден!"
        exit 1
    fi
done

# 6. Запуск системы
echo "🚀 Шаг 6: Запуск системы..."
echo "   Запускаем docker-compose..."
docker-compose up --build

# Альтернативный запуск (если предыдущий не сработал)
echo ""
echo "=========================================="
echo "Если запуск не сработал, попробуйте:"
echo "1. Запустить без кэша:"
echo "   docker-compose up --build --force-recreate"
echo ""
echo "2. Или запустить сервисы отдельно:"
echo "   docker-compose up -d"
echo ""
echo "3. Проверить логи:"
echo "   docker-compose logs -f"
echo "=========================================="