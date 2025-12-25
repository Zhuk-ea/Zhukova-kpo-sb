#!/bin/bash
# run.sh - Скрипт для запуска всего проекта

echo "Очистка предыдущих сборок..."
docker-compose down -v

echo "Сборка и запуск Docker контейнеров..."
docker-compose up --build -d

echo "Ожидание запуска всех сервисов..."
echo "Подождите 60 секунд для полного старта Kafka и Zookeeper..."
sleep 60

echo "Проверка состояния контейнеров..."
docker-compose ps

echo "Проверка логов Order Service..."
docker-compose logs --tail=20 order-service

echo "Проверка логов Kafka..."
docker-compose logs --tail=20 kafka

echo "Проверка Kafka топиков..."
docker-compose exec kafka kafka-topics --bootstrap-server localhost:29092 --list

echo "Готово! Система запущена:"
echo "   API Gateway: http://localhost:8080"
echo "   Order Service: http://localhost:8081"
echo "   Payment Service: http://localhost:8082"
echo "   PostgreSQL Accounts: localhost:5432"
echo "   PostgreSQL Orders: localhost:5433"
echo "   Kafka: localhost:9092 (для хоста), kafka:29092 (для контейнеров)"
echo ""
echo "Примеры запросов:"
echo "   Пополнить счет: curl -X POST 'http://localhost:8080/api/payments/deposit?amount=1000' -H 'X-User-Id: 123'"
echo "   Создать заказ: curl -X POST 'http://localhost:8080/api/orders' -H 'X-User-Id: 123' -H 'Content-Type: application/json' -d '{\"amount\":500,\"description\":\"Тестовый заказ\"}'"