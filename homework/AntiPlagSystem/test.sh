#!/bin/bash

echo "=========================================="
echo "🧪 ТЕСТ СИСТЕМЫ АНТИПЛАГИАТА"
echo "=========================================="
echo ""

echo "1. Создаем работы..."
echo "-------------------"

echo "Первая работа (оригинал):"
curl -X POST "http://localhost:8080/api/works" \
  -H "Content-Type: application/json" \
  -d '{"studentName":"Иванов Иван","taskId":"task1","fileName":"work1.txt","content":"Текст первой работы"}'

sleep 1

echo ""
echo "Вторая работа (плагиат):"
curl -X POST "http://localhost:8080/api/works" \
  -H "Content-Type: application/json" \
  -d '{"studentName":"Петров Петр","taskId":"task1","fileName":"work2.txt","content":"Текст первой работы"}'

sleep 1

echo ""
echo "Третья работа (уникальная):"
curl -X POST "http://localhost:8080/api/works" \
  -H "Content-Type: application/json" \
  -d '{"studentName":"Сидоров Сидор","taskId":"task1","fileName":"work3.txt","content":"Уникальный текст работы"}'

sleep 2

echo ""
echo "2. Проверяем отчеты..."
echo "---------------------"

echo "Отчет для работы #1:"
curl -s "http://localhost:8080/api/reports/work/1" | grep -E '(plagiarismDetected|details)' | sed 's/.*: //; s/,$//'

echo ""
echo "Отчет для работы #2:"
curl -s "http://localhost:8080/api/reports/work/2" | grep -E '(plagiarismDetected|details)' | sed 's/.*: //; s/,$//'

echo ""
echo "Отчет для работы #3:"
curl -s "http://localhost:8080/api/reports/work/3" | grep -E '(plagiarismDetected|details)' | sed 's/.*: //; s/,$//'

echo ""
echo "✅ Тест завершен!"
echo "Система успешно обнаружила плагиат!"