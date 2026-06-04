# 🏥 Smart Hospital Bed Tracker (PostgreSQL)

A **production-ready** real-time hospital bed management system built with **Spring Boot 3**, **WebSocket (STOMP)**, **Spring Security**, and **PostgreSQL**.

---

## 🛠️ Tech Stack

| Layer      | Technology                                    |
|------------|-----------------------------------------------|
| Backend    | Java 17 · Spring Boot 3.2                     |
| ORM        | Spring Data JPA · Hibernate                   |
| **DB**     | **PostgreSQL 14+** (H2 for tests)             |
| Real-Time  | WebSocket · STOMP · SockJS                    |
| Security   | Spring Security · HTTP Basic Auth             |
| Validation | Jakarta Bean Validation                       |
| Boilerplate| Lombok                                        |
| Frontend   | Vanilla HTML/CSS/JS (served as static resource)|

---

## 📁 Project Structure

```
smart-bed-tracker/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/hospital/smartbedtracker/
    │   │   ├── SmartBedTrackerApplication.java
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java
    │   │   │   └── WebSocketConfig.java
    │   │   ├── controller/
    │   │   │   ├── BedController.java
    │   │   │   ├── PatientController.java
    │   │   │   └── WardController.java
    │   │   ├── dto/
    │   │   │   ├── AdmitPatientRequest.java
    │   │   │   ├── BedStatsResponse.java
    │   │   │   └── CreateBedRequest.java
    │   │   ├── entity/
    │   │   │   ├── Bed.java       ← uses PostgreSQL SEQUENCE
    │   │   │   ├── Patient.java   ← uses PostgreSQL SEQUENCE
    │   │   │   └── Ward.java      ← uses PostgreSQL SEQUENCE
    │   │   ├── enums/
    │   │   │   ├── BedStatus.java
    │   │   │   └── PatientStatus.java
    │   │   ├── exception/
    │   │   │   ├── BedNotAvailableException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── ResourceNotFoundException.java
    │   │   ├── init/
    │   │   │   └── DataInitializer.java   ← seeds 4 wards + 57 beds
    │   │   ├── repository/
    │   │   │   ├── BedRepository.java
    │   │   │   ├── PatientRepository.java
    │   │   │   └── WardRepository.java
    │   │   └── service/
    │   │       ├── BedService.java
    │   │       ├── PatientService.java
    │   │       └── WardService.java
    │   └── resources/
    │       ├── application.properties   ← PostgreSQL config
    │       ├── schema.sql               ← reference DDL (optional manual run)
    │       └── static/
    │           └── index.html           ← live dashboard UI
    └── test/
        └── java/com/hospital/smartbedtracker/
            └── SmartBedTrackerApplicationTests.java  ← uses H2 PG-compat mode
```

---

## ⚙️ Setup

### 1. Install PostgreSQL

```bash
# Ubuntu / Debian
sudo apt install postgresql postgresql-contrib

# macOS (Homebrew)
brew install postgresql@16 && brew services start postgresql@16

# Windows: download from https://www.postgresql.org/download/windows/
```

### 2. Create the Database

```bash
# Connect as postgres superuser
psql -U postgres

# Inside psql:
CREATE DATABASE hospital_db;
\q
```

Or in one line:
```bash
psql -U postgres -c "CREATE DATABASE hospital_db;"
```

### 3. Configure `application.properties`

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hospital_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_ACTUAL_PASSWORD
```

Change usernames/passwords in the security section too if desired.

### 4. Run the Application

```bash
mvn spring-boot:run
```

The **first run** automatically seeds 4 wards and 57 beds.  
➡️ **After first run**, set `app.data.init=false` in `application.properties` to prevent re-seeding.

### 5. Open the Dashboard

Visit **http://localhost:8080** — the live bed-tracker dashboard loads instantly.

---

## 🔐 Default Users

| Role  | Username | Password   | Access Level              |
|-------|----------|------------|---------------------------|
| Nurse | `nurse`  | `nurse123` | All beds & patients APIs  |
| Admin | `admin`  | `admin123` | Full access incl. wards   |

Public (no auth): `/api/beds/stats`, `/ws/**`, `/`

---

## 📡 REST API Reference

### Beds — `/api/beds`

| Method | Endpoint                    | Auth   | Description           |
|--------|-----------------------------|--------|-----------------------|
| GET    | `/api/beds`                 | USER   | All beds              |
| GET    | `/api/beds/{id}`            | USER   | Single bed            |
| GET    | `/api/beds/available`       | USER   | Available beds        |
| GET    | `/api/beds/stats`           | Public | Occupancy stats       |
| GET    | `/api/beds/ward/{wardId}`   | USER   | Beds in a ward        |
| POST   | `/api/beds`                 | ADMIN  | Create a bed          |
| PUT    | `/api/beds/{id}/assign`     | USER   | Mark as occupied      |
| PUT    | `/api/beds/{id}/release`    | USER   | Mark as available     |
| PUT    | `/api/beds/{id}/maintenance`| USER   | Mark for maintenance  |
| PUT    | `/api/beds/{id}/reserve`    | USER   | Reserve a bed         |
| DELETE | `/api/beds/{id}`            | ADMIN  | Delete a bed          |

### Patients — `/api/patients`

| Method | Endpoint                              | Auth | Description          |
|--------|---------------------------------------|------|----------------------|
| GET    | `/api/patients`                       | USER | All patients         |
| GET    | `/api/patients/{id}`                  | USER | Single patient       |
| GET    | `/api/patients/admitted`              | USER | Admitted only        |
| POST   | `/api/patients/admit/{bedId}`         | USER | Admit to a bed       |
| PUT    | `/api/patients/{id}/discharge`        | USER | Discharge patient    |
| PUT    | `/api/patients/{id}/transfer/{bedId}` | USER | Transfer patient     |

### Wards — `/api/wards` *(ADMIN only)*

| Method | Endpoint          | Description   |
|--------|-------------------|---------------|
| GET    | `/api/wards`      | All wards     |
| GET    | `/api/wards/{id}` | Single ward   |
| POST   | `/api/wards`      | Create ward   |
| PUT    | `/api/wards/{id}` | Update ward   |
| DELETE | `/api/wards/{id}` | Delete ward   |

---

## 📡 WebSocket

Connect to `/ws` (SockJS) and subscribe to real-time channels:

```javascript
const socket = new SockJS('/ws');
const client = Stomp.over(socket);
client.connect({}, () => {
    // Real-time bed status changes
    client.subscribe('/topic/beds', msg => {
        const bed = JSON.parse(msg.body);
        console.log('Bed updated:', bed);
    });
    // Real-time patient events
    client.subscribe('/topic/patients', msg => {
        const patient = JSON.parse(msg.body);
        console.log('Patient event:', patient);
    });
});
```

---

## 🧪 Tests

Tests use **H2 in-memory DB in PostgreSQL compatibility mode** — no real PostgreSQL needed:

```bash
mvn test
```

---

## 🚀 Build Executable JAR

```bash
mvn clean package -DskipTests
java -jar target/smart-bed-tracker-0.0.1-SNAPSHOT.jar
```

---

## 🐘 PostgreSQL-Specific Notes

| Item | Detail |
|------|--------|
| ID strategy | `SEQUENCE` (canonical PG approach, not `IDENTITY`) |
| Dialect | `org.hibernate.dialect.PostgreSQLDialect` |
| Reserved word fix | `disease` column mapped to `diagnosis` in patients table |
| DDL reference | `src/main/resources/schema.sql` — for manual setup or review |
| `ddl-auto` | Set to `update` — safe for dev; use `validate` in production |
| Timezone | PostgreSQL stores `TIMESTAMP WITHOUT TIME ZONE`; LocalDateTime maps correctly |

