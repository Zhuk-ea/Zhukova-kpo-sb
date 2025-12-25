# Гоzон - Модуль обработки заказов и платежей

## Техническое описание

### Обзор
Микросервисная система для обработки заказов и платежей в интернет-магазине Гоzон. Реализована с использованием асинхронной архитектуры, гарантиями доставки сообщений и идемпотентной обработкой операций.

### Архитектурный стек
- **Java 21** + **Spring Boot 3.2.0**
- **PostgreSQL 15** для БД
- **Apache Kafka** для асинхронной коммуникации
- **Spring Cloud Gateway** как API Gateway
- **Docker Compose** для запуска

### Компоненты системы

#### 1. API Gateway (порт 8080)
- Единая точка входа для всех клиентских запросов
- Маршрутизация:
    - `/api/orders/**` → Order Service
    - `/api/payments/**` → Payment Service

#### 2. Order Service (порт 8081)
- Управление жизненным циклом заказов
- Паттерн **Transactional Outbox** для публикации событий
- База данных: `order_db` (PostgreSQL на порту 5433)

#### 3. Payment Service (порт 8082)
- Управление счетами пользователей и обработка платежей
- Паттерны **Transactional Inbox + Outbox** для идемпотентности
- CAS (Compare and Swap) операции для атомарных изменений баланса
- База данных: `account_db` (PostgreSQL на порту 5432)

#### 4. Инфраструктурные сервисы
- **Zookeeper** (порт 2181) - для координации Kafka
- **Kafka** (порты 9092, 29092) - брокер сообщений
- **PostgreSQL x2** - отдельные базы для каждого сервиса

## Установка и запуск

### Предварительные требования
- Docker 20.10+
- Docker Compose 2.0+

### Быстрый старт
```bash
# Очистка и запуск всех сервисов
docker-compose down -v --remove-orphans
docker-compose up --build -d

# Проверка статуса
docker-compose ps
```

Система будет доступна по адресам:
- **API Gateway**: http://localhost:8080
- **Order Service**: http://localhost:8081
- **Payment Service**: http://localhost:8082

## API Endpoints

### Payment Service API

#### Пополнение счета
```bash
POST /api/payments/deposit?amount={сумма}
Header: X-User-Id: {id_пользователя}
```

#### Получение баланса
```bash
GET /api/payments/balance
Header: X-User-Id: {id_пользователя}
```

### Order Service API

#### Создание заказа
```bash
POST /api/orders
Header: X-User-Id: {id_пользователя}
Content-Type: application/json

Body:
{
  "amount": {сумма},
  "description": "Описание заказа"
}
```

#### Получение заказов пользователя
```bash
GET /api/orders
Header: X-User-Id: {id_пользователя}
```

#### Получение конкретного заказа
```bash
GET /api/orders/{orderId}
Header: X-User-Id: {id_пользователя}
```

## Архитектурные паттерны

### 1. Transactional Outbox (Order Service)
Паттерн для гарантированной доставки событий о создании заказов в Kafka. Событие сохраняется в базу данных в рамках той же транзакции, что и заказ, затем асинхронно отправляется в Kafka.

### 2. Transactional Inbox (Payment Service)
Механизм идемпотентной обработки входящих сообщений из Kafka. Каждое сообщение проверяется по idempotency_key перед обработкой, предотвращая дублирующее списание средств.

### 3. CAS (Compare and Swap)
Атомарные операции обновления баланса с проверкой версии, предотвращающие состояние гонки при параллельных операциях.

## Поток данных

### Сценарий создания и оплаты заказа

1. **Клиент** → POST /api/orders → **Order Service**
2. **Order Service**:
    - Сохраняет заказ в БД `order_db`
    - Сохраняет событие `ORDER_CREATED` в таблицу `order_outbox`
    - Возвращает ответ клиенту
3. **Outbox Poller** (каждые 5 секунд):
    - Находит PENDING события в `order_outbox`
    - Отправляет их в Kafka топик `order.created.event`
4. **Kafka Consumer** (Payment Service):
    - Получает сообщение из `order.created.event`
    - Проверяет идемпотентность через таблицу `payment_inbox`
    - Выполняет списание средств через CAS операцию
    - Сохраняет результат в `payment_inbox`
    - Сохраняет событие в `payment_outbox`
5. **Payment Outbox Poller**:
    - Отправляет результат в Kafka топик `payment.processed.event`
6. **Kafka Consumer** (Order Service):
    - Получает сообщение из `payment.processed.event`
    - Обновляет статус заказа в БД

## Базы данных

### Order Service (order_db)
- **orders** - таблица заказов
    - id, user_id, amount, description, status, created_at
- **order_outbox** - таблица исходящих событий
    - id, aggregate_id, event_type, payload, status, created_at, sent_at

### Payment Service (account_db)
- **accounts** - таблица счетов пользователей
    - id, user_id, balance, version (для CAS)
- **payment_inbox** - таблица входящих сообщений (идемпотентность)
    - idempotency_key, order_id, user_id, status, processed_at
- **payment_outbox** - таблица исходящих событий
    - id, order_id, user_id, payload, status, created_at, sent_at

## Гарантии

### Exactly-Once семантика
- Идемпотентная обработка через `payment_inbox`
- CAS операции для атомарного обновления баланса
- Отслеживание статуса обработки каждого сообщения

### At-Least-Once доставка
- Transactional Outbox гарантирует сохранение события
- Периодический опрос и повторная отправка PENDING событий
- Отслеживание статуса отправки (SENT/FAILED)

## Мониторинг и отладка

### Проверка состояния Kafka
```bash
# Список топиков
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:29092

# Просмотр сообщений
docker-compose exec kafka kafka-console-consumer \
  --bootstrap-server localhost:29092 \
  --topic order.created.event \
  --from-beginning
```

### Проверка баз данных
```bash
# Order Service
docker-compose exec db_order psql -U postgres -d order_db -c "SELECT * FROM orders;"

# Payment Service  
docker-compose exec db_account psql -U postgres -d account_db -c "SELECT * FROM accounts;"
docker-compose exec db_account psql -U postgres -d account_db -c "SELECT * FROM payment_inbox;"
```

### Логи сервисов
```bash
# Order Service
docker-compose logs order-service

# Payment Service
docker-compose logs payment-service

# Kafka
docker-compose logs kafka
```

## Тестирование

### Базовый сценарий
```bash
# 1. Пополнение счета
curl -X POST 'http://localhost:8080/api/payments/deposit?amount=5000' -H 'X-User-Id: 123'

# 2. Создание заказа
curl -X POST 'http://localhost:8080/api/orders' \
  -H 'X-User-Id: 123' \
  -H 'Content-Type: application/json' \
  -d '{"amount": 1500.99, "description": "Тестовый заказ"}'

# 3. Проверка статуса
sleep 5
curl -H 'X-User-Id: 123' 'http://localhost:8080/api/orders'
```
