# Synthetic Human Core Project

Учебный проект по созданию единого стартера (`synthetic-human-core-starter`) для унификации разработки программного обеспечения для всех моделей андроидов компании "Вейланд-Ютани". Проект также включает в себя демонстрационный сервис-эмулятор `bishop-prototype`.

## Описание

`synthetic-human-core-starter` предоставляет следующий функционал "из коробки":

*   **Модуль приема и исполнения команд:** Обрабатывает команды с разными приоритетами, используя очередь для некритичных задач.
*   **Модуль аудита:** Позволяет протоколировать действия андроида с помощью аннотации `@WeylandWatchingYou` с выводом в консоль или отправкой в Kafka.
*   **Модуль мониторинга:** Публикует метрики для Prometheus через Actuator (занятость очереди, количество выполненных задач).
*   **Модуль обработки ошибок:** Предоставляет стандартизированные ответы об ошибках для REST API.

`bishop-prototype` — это пример использования стартера, реализующий REST API для приема команд и демонстрации всех его возможностей.

## Технологический стек

*   Java 17
*   Spring Boot 3
*   Maven
*   Kafka
*   Docker (для локального развертывания Kafka)
*   Micrometer & Actuator (для метрик)
*   AOP (для аудита)

## Требования

*   JDK 17 или выше
*   Apache Maven 3.8+
*   Docker и Docker Compose

## Быстрый старт

1.  **Клонируйте репозиторий:**
    ```bash
    git clone <URL_репозитория>
    cd synthetic-human-project
    ```

2.  **Поднимите Kafka и Zookeeper:**
    Убедитесь, что Docker запущен, и выполните команду:
    ```bash
    docker-compose up -d
    ```

3.  **Соберите проект:**
    Проект состоит из двух модулей. Команда ниже соберет стартер и установит его в локальный Maven-репозиторий, а затем соберет демонстрационное приложение.
    ```bash
    mvn clean install
    ```

4.  **Запустите приложение-эмулятор:**
    ```bash
    java -jar bishop-prototype/target/bishop-prototype-0.0.1-SNAPSHOT.jar
    ```
    Приложение будет доступно по адресу `http://localhost:8080`.

## Использование API

### Отправка команды

Отправьте POST-запрос на эндпоинт `/api/android/command`.

**Пример (COMMON Priority):**
```bash
curl -X POST http://localhost:8080/api/android/command \
-H "Content-Type: application/json" \
-d '{
    "description": "Проверить состояние энергоблока",
    "priority": "COMMON",
    "author": "Эллен Рипли",
    "time": "2024-05-21T10:00:00Z"
}'

**Пример (CRITICAL Priority):**
curl -X POST http://localhost:8080/api/android/command \
-H "Content-Type: application/json" \
-d '{
    "description": "Активировать протокол самоуничтожения",
    "priority": "CRITICAL",
    "author": "Картер Бёрк",
    "time": "2024-05-21T11:00:00Z"
}'