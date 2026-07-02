# REST API Layer (Java & Spring Boot)

This document provides the application variables, JPA Entity mappings, and exposed endpoints.

## 🏗️ System Architecture

The following diagram illustrates the 3-tier architecture of the PRMS application, including its controllers, JPA entities, database failover strategy, and front-end integration.

```mermaid
graph TD
    classDef client fill:#eef2f7,stroke:#3b82f6,stroke-width:2px,color:#1e3a8a;
    classDef api fill:#ecfdf5,stroke:#10b981,stroke-width:2px,color:#065f46;
    classDef db fill:#fff7ed,stroke:#f97316,stroke-width:2px,color:#7c2d12;
    classDef config fill:#faf5ff,stroke:#8b5cf6,stroke-width:2px,color:#4c1d95;

    subgraph Client_Layer ["Client Layer"]
        React["React UI (Port 3000)"]:::client
    end

    subgraph API_Layer ["Spring Boot REST API (Port 8080)"]
        subgraph Controllers ["Controllers"]
            PC["PatientController"]:::api
            DC["DoctorController"]:::api
            AC["AppointmentController"]:::api
            MC["MedicalRecordController"]:::api
        end

        subgraph Repositories ["Spring Data JPA Repositories"]
            PR["PatientRepository"]:::api
            DR["DoctorRepository"]:::api
            AR["AppointmentRepository"]:::api
            MR["MedicalRecordRepository"]:::api
        end

        subgraph Models ["JPA Entities"]
            Patient["Patient Entity"]:::api
            Doctor["Doctor Entity"]:::api
            Appointment["Appointment Entity"]:::api
            MedRecord["MedicalRecord Entity"]:::api
        end

        DataSourceConfig["DataSourceConfig<br/>(Dynamic Failover Route)"]:::config
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

    %% DataSource Config Routing to Database
    PR --> DataSourceConfig
    DR --> DataSourceConfig
    AR --> DataSourceConfig
    MR --> DataSourceConfig

    DataSourceConfig -->|Active Connection| MySQL
    DataSourceConfig -->|Fallback Connection| H2
```


## 🛠️ Configuration Settings (`application.properties`)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/prms_db?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_secure_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
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
