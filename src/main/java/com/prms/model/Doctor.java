package com.prms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    private String phone;

    @Column(unique = true)
    private String email;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Boilerplate Getters and Setters
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer id) { this.doctorId = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String fn) { this.firstName = fn; }
    public String getLastName() { return lastName; }
    public void setLastName(String ln) { this.lastName = ln; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String spec) { this.specialization = spec; }
    public String getPhone() { return phone; }
    public void setPhone(String p) { this.phone = p; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { this.isActive = active; }
}
