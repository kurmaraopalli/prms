package com.prms.controller;

import com.prms.model.MedicalRecord;
import com.prms.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medical-records")
@CrossOrigin(origins = "http://localhost:3000")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @GetMapping
    public List<MedicalRecord> fetchAll() {
        return medicalRecordRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> fetchById(@PathVariable Integer id) {
        return medicalRecordRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MedicalRecord registerNew(@RequestBody MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable Integer id, @RequestBody MedicalRecord details) {
        return medicalRecordRepository.findById(id)
                .map(record -> {
                    record.setPatient(details.getPatient());
                    record.setDoctor(details.getDoctor());
                    record.setVisitDate(details.getVisitDate());
                    record.setDiagnosis(details.getDiagnosis());
                    record.setPrescription(details.getPrescription());
                    record.setNotes(details.getNotes());
                    MedicalRecord updated = medicalRecordRepository.save(record);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMedicalRecord(@PathVariable Integer id) {
        if (!medicalRecordRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        medicalRecordRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
