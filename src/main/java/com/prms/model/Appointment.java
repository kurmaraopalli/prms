package com.prms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    @Column(name = "status")
    private String status = "Scheduled";

    @Column(name = "symptoms_brief")
    private String symptomsBrief;

    // Boilerplate Getters and Setters
    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer id) { this.appointmentId = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient p) { this.patient = p; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor d) { this.doctor = d; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime date) { this.appointmentDate = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSymptomsBrief() { return symptomsBrief; }
    public void setSymptomsBrief(String symptoms) { this.symptomsBrief = symptoms; }
}
