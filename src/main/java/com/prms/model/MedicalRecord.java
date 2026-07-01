package com.prms.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "diagnosis", nullable = false)
    private String diagnosis;

    private String prescription;

    private String notes;

    // Boilerplate Getters and Setters
    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer id) { this.recordId = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient p) { this.patient = p; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor d) { this.doctor = d; }
    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate date) { this.visitDate = date; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getPrescription() { return prescription; }
    public void setPrescription(String pres) { this.prescription = pres; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
