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
