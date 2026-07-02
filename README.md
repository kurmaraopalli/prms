# REST API Layer (Java & Spring Boot)

This document provides the application variables, JPA Entity mappings, and exposed endpoints.

## 🏗️ System Architecture

The following diagram illustrates the 3-tier architecture of the PRMS application, including its controllers, JPA entities, database failover strategy, and front-end integration.

```mermaid
graph TD
    classDef client fill:#eff6ff,stroke:#3b82f6,stroke-width:2px,color:#1e40af;
    classDef controller fill:#ecfeff,stroke:#06b6d4,stroke-width:2px,color:#155e75;
    classDef repo fill:#f0fdf4,stroke:#22c55e,stroke-width:2px,color:#166534;
    classDef model fill:#faf5ff,stroke:#a855f7,stroke-width:2px,color:#6b21a8;
    classDef config fill:#fff1f2,stroke:#f43f5e,stroke-width:2px,color:#9f1239;
    classDef db fill:#fffbeb,stroke:#f59e0b,stroke-width:2px,color:#78350f;

    subgraph Client_Layer ["Client Layer"]
        React["React UI (Port 3000)"]:::client
    end

    subgraph API_Layer ["Spring Boot REST API (Port 8080)"]
        subgraph Controllers ["Controllers"]
            PC["PatientController"]:::controller
            DC["DoctorController"]:::controller
            AC["AppointmentController"]:::controller
            MC["MedicalRecordController"]:::controller
        end

        subgraph Repositories ["Spring Data JPA Repositories"]
            PR["PatientRepository"]:::repo
            DR["DoctorRepository"]:::repo
            AR["AppointmentRepository"]:::repo
            MR["MedicalRecordRepository"]:::repo
        end

        subgraph Models ["JPA Entities"]
            Patient["Patient Entity"]:::model
            Doctor["Doctor Entity"]:::model
            Appointment["Appointment Entity"]:::model
            MedRecord["MedicalRecord Entity"]:::model
        end

        DataSourceConfig["DataSourceConfig<br/>(Dynamic Failover Route)"]:::config
        DS["DataSource Bean<br/>(Connection Provider)"]:::config
    end

    subgraph Database_Layer ["Database Layer"]
        MySQL[("MySQL Database<br/>(Primary / Port 3306)")]:::db
        H2[("H2 Database<br/>(File Failover Fallback)")]:::db
    end

    %% Client communicating with Controllers
    React -->|HTTP REST API / CORS| PC
    React -->|HTTP REST API / CORS| DC
    React -->|HTTP REST API / CORS| AC
    React -->|HTTP REST API / CORS| MC

    %% Controller calls Repository
    PC --> PR
    DC --> DR
    AC --> AR
    MC --> MR

    %% Repository maps Entity
    PR -.-> Patient
    DR -.-> Doctor
    AR -.-> Appointment
    MR -.-> MedRecord

    %% Appointment & MedicalRecord references Patients / Doctors
    Appointment --> Patient
    Appointment --> Doctor
    MedRecord --> Patient
    MedRecord --> Doctor

    %% DataSource Config defines DataSource Bean
    DataSourceConfig -->|Configures & Instantiates| DS

    %% Repositories use DataSource to connect
    PR --> DS
    DR --> DS
    AR --> DS
    MR --> DS

    %% DataSource Routes connections
    DS -->|Active Connection| MySQL
    DS -->|Fallback Connection| H2
```


## 🛠️ Configuration Settings (`application.properties`)

```properties
spring.application.name=prms

# MySQL configuration properties
prms.db.mysql.url=jdbc:mysql://localhost:3306/prms_db?useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
prms.db.mysql.username=root
prms.db.mysql.password=your_secure_password
prms.db.mysql.driver-class-name=com.mysql.cj.jdbc.Driver
prms.db.mysql.dialect=org.hibernate.dialect.MySQLDialect

# H2 configuration properties (flat-file fallback)
prms.db.h2.url=jdbc:h2:file:./prms_h2_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
prms.db.h2.username=sa
prms.db.h2.password=welcome
prms.db.h2.driver-class-name=org.h2.Driver
prms.db.h2.dialect=org.hibernate.dialect.H2Dialect

# Common JPA / Hibernate settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console (for debugging flat-file database contents)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=true
```

## 💻 Core Code Base

### 1. Data Object Mapping (`model/Patient.java`)
```java
package com.prms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    private String phone;

    @Column(unique = true)
    private String email;

    private String address;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Gender { Male, Female, Other }

    // Boilerplate Getters and Setters
    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer id) { this.patientId = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String fn) { this.firstName = fn; }
    public String getLastName() { return lastName; }
    public void setLastName(String ln) { this.lastName = ln; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dob) { this.dateOfBirth = dob; }
    public Gender getGender() { return gender; }
    public void setGender(Gender g) { this.gender = g; }
    public String getPhone() { return phone; }
    public void setPhone(String p) { this.phone = p; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getAddress() { return address; }
    public void setAddress(String a) { this.address = a; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String ec) { this.emergencyContact = ec; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

### 2. JPA Repository Hook (`repository/PatientRepository.java`)
```java
package com.prms.repository;

import com.prms.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
}
```

### 3. Web Service Controller Layer (`controller/PatientController.java`)
```java
package com.prms.controller;

import com.prms.model.Patient;
import com.prms.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@CrossOrigin(origins = "http://localhost:3000") // Explicit React Port Routing
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping
    public List<Patient> fetchAll() {
        return patientRepository.findAll();
    }

    @PostMapping
    public Patient registerNew(@RequestBody Patient patient) {
        return patientRepository.save(patient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePatient(@PathVariable Integer id) {
        if (!patientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        patientRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
```
