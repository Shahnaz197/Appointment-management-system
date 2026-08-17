# MEDIBOOK — Hospital Appointment System

A production-ready REST API built with Spring Boot for managing hospital appointments, featuring performance optimizations, security, and concurrent booking management.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 4.1.0 |
| Database | MySQL 8.0, Spring Data JPA |
| Cache | Redis |
| Security | JWT, Spring Security, BCrypt |
| Rate Limiting | Bucket4j |
| Frontend | HTML, CSS, JavaScript |

---

## Performance Features

- **Redis Caching** — doctor profiles cached with 10 minute TTL, reducing database load significantly
- **Database Indexing** — indexes on `specialization`, `appointment_date`, `status`, `doctor_id`, `patient_id` for faster queries
- **Pagination** — paginated doctor listings for scalable data retrieval
- **Async Notifications** — email notifications processed asynchronously, improving API response time
- **Thread-Safe Booking** — synchronized concurrent booking prevents race conditions
- **Rate Limiting** — 10 requests per minute per IP using Bucket4j

---

## Security Features

- JWT authentication with 24 hour token expiry
- BCrypt password encryption
- Protected API endpoints
- Rate limiting to prevent abuse
- CVE-2024-31033 resolved by upgrading jjwt to 0.12.6

---

## Features

- Patient registration and management
- Doctor management with specialization filtering
- Appointment booking with conflict detection
- Waitlist management using Queue data structure
- Async email notifications on booking/cancellation
- Role based access (PATIENT, DOCTOR, ADMIN)
- Paginated doctor listings
- Global exception handling with custom exceptions

---

## Project Structure

src/main/java/com/hospital/appointment_system/
├── controllers/
│ ├── AuthController.java
│ ├── PatientController.java
│ ├── DoctorController.java
│ └── AppointmentController.java
├── services/
│ ├── AuthService.java
│ ├── PatientService.java
│ ├── DoctorService.java
│ ├── AppointmentService.java
│ └── NotificationService.java
├── repositories/
│ ├── UserRepository.java
│ ├── PatientRepository.java
│ ├── DoctorRepository.java
│ └── AppointmentRepository.java
├── models/
│ ├── User.java
│ ├── Patient.java
│ ├── Doctor.java
│ ├── Appointment.java
│ ├── Role.java
│ └── AppointmentStatus.java
├── security/
│ ├── JwtUtil.java
│ ├── JwtFilter.java
│ └── RateLimitFilter.java
├── exceptions/
│ ├── GlobalExceptionHandler.java
│ ├── PatientNotFoundException.java
│ ├── DoctorNotFoundException.java
│ └── AppointmentConflictException.java
└── config/
├── SecurityConfig.java
├── RedisConfig.java
└── AsyncConfig.java

---

## 🛠️ Setup & Installation

### Prerequisites
- Java 17+
- MySQL 8.0+
- Redis

### 1. Clone the repository
```bash
git clone https://github.com/Shahnaz197/hospital-appointment-system.git
cd hospital-appointment-system
```

### 2. Create MySQL database
```sql
CREATE DATABASE hospital_db;
```

### 3. Configure application properties
copy `src/main/resources/application.properties.example` to `application.properties` and update:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Start Redis
```bash
redis-server
```

### 5. Run the application
```bash
./mvnw spring-boot:run
```

### 6. Access the application

http://localhost:8080

---

##  API Endpoints

### Auth
POST /api/auth/register → register user
POST /api/auth/login → login and get JWT token

### Patients

POST /api/patients → create patient
GET /api/patients → get all patients
GET /api/patients/{id} → get patient by id
PUT /api/patients/{id} → update patient
DELETE /api/patients/{id} → delete patient

### Doctors

POST /api/doctors → add doctor
GET /api/doctors → get all doctors
GET /api/doctors/{id} → get doctor by id
GET /api/doctors/available → get available doctors
GET /api/doctors/specialization/{spec} → filter by specialization
GET /api/doctors/paginated → paginated doctor list
PUT /api/doctors/{id} → update doctor
DELETE /api/doctors/{id} → delete doctor

### Appointments

POST /api/appointments/book → book appointment
GET /api/appointments → get all appointments
GET /api/appointments/patient/{id} → patient appointments
GET /api/appointments/doctor/{id} → doctor appointments
GET /api/appointments/doctor/{id}/date/{date} → by doctor and date
PUT /api/appointments/{id}/cancel → cancel appointment
PUT /api/appointments/{id}/complete → complete appointment
GET /api/appointments/waitlist/size → waitlist size

---

## 🎯 Key Technical Decisions

**Why synchronized for booking?**
concurrent booking requests could cause race conditions where two patients book the same slot simultaneously. `synchronized` ensures only one booking processes at a time.

**Why Redis for caching?**
doctor profiles are frequently read but rarely updated. caching them in Redis reduces repeated database queries significantly.

**Why Queue for waitlist?**
FIFO behavior ensures fairness — first patient to join waitlist gets notified first when a slot opens.

**Why async notifications?**
sending emails synchronously would block the API response. `@Async` runs notifications in a background thread so users get instant response.

---

## Author

**Shahnaz** 
- GitHub: [@Shahnaz197](https://github.com/Shahnaz197)
