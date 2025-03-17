Простое приложение для управления задачами с аутентификацией пользователей, построенное на Spring Boot и PostgreSQL.

##  Особенности

- Регистрация и аутентификация пользователей (JWT)
- CRUD операции для задач
- Комментарии к задачам
- Ролевая модель (USER/ADMIN)
- Миграции баз данных через Flyway
- Docker-контейнеризация

##  Технологии

- **Backend**: Java 17, Spring Boot 3.4.3
- **База данных**: PostgreSQL 13
- **Миграции**: Flyway 10.0.1
- **Безопасность**: Spring Security, JWT
- **Инфраструктура**: Docker, Docker Compose

## Быстрый старт

### Предварительные требования
- Docker 20.10+
- Docker Compose 2.20+

### Установка

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/syntheticdiva/tasks.git
   cd task-management
Создайте файл .env в корне проекта:

text
DB_NAME=task
DB_USER=postgres
DB_PASS=111
JWT_SECRET=mySuperSecretKeyWithAtLeast32Characters123
Запустите приложение:

bash
docker-compose up --build
Приложение будет доступно на http://localhost:8080