# 📘 Система учёта рабочего времени сотрудников

Spring Boot приложение с авторизацией по JWT, CRUD-операциями над сотрудниками и рабочим временем, импортом/экспортом
CSV и запуском в Docker-контейнере.

---

## 📦 Быстрый старт

### 🔑 1. Генерация JWT ключа

Для переменной `JWT_SIGNING_KEY` в `.env` можно использовать OpenSSL:

```bash
openssl rand -base64 32
```

Или Java:

```bash
echo $(uuidgen | base64)
```

Вставьте результат в `.env`:

```
JWT_SIGNING_KEY=your_generated_key_here
```

---

### 🐳 2. Запуск через Docker Compose

#### 📝 2.1. Создайте `.env` на основе `envexample`:

```env
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

JWT_SIGNING_KEY=your_secret_jwt_key

ADMIN_USERNAME=admin
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=adminpass
```

#### ⚙️ 2.2. Запуск:

```bash
docker compose up -d
```

### 🌐 3. Если вы не хотите собирать проект вручную, вы можете просто использовать уже готовый образ, загруженный в Docker Hub.

Скачать готовый образ из Docker Hub:
   
docker pull <ваш-dockerhub-логин>/accounting-time:latest

## 🛠 4. Локальная сборка (если нужно)

```bash
mvn clean package
docker build -t accounting-time:latest .
```

---

## 🔐 5. Аутентификация

### 🔸 Регистрация

**POST** `/auth/reg`

```json
{
  "employeeName": "ivanov",
  "password": "superSecure456",
  "position": "CEO",
  "chiefId": null,
  "departmentId": 2
}
```

### 🔸 Логин

**POST** `/auth/login`

```json
{
  "employeeName": "ivanov",
  "password": "superSecure456"
}
```

📥 Ответ:

```json
{
  "token": "Bearer eyJhbGciOiJIUzI1NiIs..."
}
```

### 🔄 Обновление токена

**GET** `/auth/token/refresh`

```http
Authorization: Bearer {старый_токен}
```

---

## 📋 6. Работа с токенами в Postman

Настрой переменные окружения в Postman:

- `{{access_token}}` — для хранения JWT
- `Authorization` — в Headers: `Bearer {{access_token}}`

---

## 📚 7. Эндпоинты

### 👤 `/employees` (доступен только ADMIN)

| Метод  | URI               | Описание           |
|--------|-------------------|--------------------|
| GET    | /employees        | Получить всех      |
| GET    | /employees/{id}   | Получить по ID     |
| POST   | /employees/create | Создать сотрудника |
| PUT    | /employees/{id}   | Обновить           |
| DELETE | /employees/{id}   | Удалить            |

---

### ⏱ `/entry` (доступ ADMIN и USER)

| Метод  | URI                             | Описание                                                                     |
|--------|---------------------------------|------------------------------------------------------------------------------|
| GET    | /entry/by-employee/{employeeId} | Все записи сотрудника (сотрудник или начальник любого уровня)                |
| GET    | /entry/{id}                     | Получить запись (сотрудник или его начальник)                                |
| POST   | /entry/create                   | Добавить запись (может создать сотрудник или его непосредственный начальник) |
| PUT    | /entry/{id}                     | Обновить запись (сотрудник или его начальник)                                |
| DELETE | /entry/{id}                     | Удалить запись (сотрудник или его начальник)                                 |

---

### 📤 `/csv` (импорт/экспорт CSV, доступ ADMIN и USER)

| Метод | URI         | Описание                                                                         |
|-------|-------------|----------------------------------------------------------------------------------|
| POST  | /csv/import | Импорт CSV (только свои записи, или записи подчинённого)                         |
| GET   | /csv/export | Экспорт CSV (доступны записи сотрудника и всех его подчинённых по всей иерархии) |

---

## 🧪 8. Примеры из Postman

### 👤 Регистрация пользователей

**POST** `/auth/reg`

  {
    "employeeName": "ivanov",
    "password": "superSecure456",
    "position": "CEO",
    "chiefId": null,
    "departmentId": 2
  }

  {
    "employeeName": "sidorov",
    "password": "qwerty9876",
    "position": "DEVELOPER",
    "chiefId": 5,
    "departmentId": 1
  }

  {
    "employeeName": "volkov",
    "password": "123frk",
    "position": "DEVELOPER",
    "chiefId": 6,
    "departmentId": 1
  }

  {
    "employeeName": "novikova",
    "password": "rfkt456",
    "position": "ANALYST",
    "chiefId": 5,
    "departmentId": 3
  }

### ⏱ Создание записи времени

**POST** `/entry/create`

Иванов вносит записи за себя
    {
    "workDate": "2025-07-10",
    "hoursWorked": 8.0,
    "employee": {
    "id": 5
    },
    "createdBy": {
    "id": 5
    }
    }

    {
      "workDate": "2025-07-11",
      "hoursWorked": 9.0,
      "employee": {
        "id": 5
      },
      "createdBy": {
        "id": 5
      }
    }

    Иванов вносит запись за Сидорова (его непосредственного подчинённого)

    {
      "workDate": "2025-07-10",
      "hoursWorked": 7.5,
      "employee": {
        "id": 6
      },
      "createdBy": {
        "id": 5
      }
    }

    Сидоров вносит себе запись 

    {
      "workDate": "2025-07-10",
      "hoursWorked": 8.0,
      "employee": {
        "id": 6
      },
      "createdBy": {
        "id": 6
      }
    }

    Новикова вносит запись за себя 

    {
      "workDate": "2025-07-10",
      "hoursWorked": 7.0,
      "employee": {
        "id": 8
      },
      "createdBy": {
        "id": 8
      }
    }

    Волков вносит за себя 
    {
    "workDate": "2025-07-11",
    "hoursWorked": 5.0,
    "employee": {
        "id": 7
    },
    "createdBy": {
        "id": 7
    }
    }

🧠 Примечания:

- Только одна запись в день на сотрудника.
- Импортировать можно только свои записи или записи подчинённого.
- Просматривать и экспортировать можно все записи своих подчинённых по всей иерархии.

---

## 🌍 Подключение к контейнеру

Если приложение работает в контейнере, используйте IP хоста или доменное имя вместо `localhost`, например:

```
http://192.168.0.100:8080
```
