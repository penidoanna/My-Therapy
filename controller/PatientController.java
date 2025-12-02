package com.mycompany.mytherapy.controller;

import com.mycompany.mytherapy.model.Patient;
import com.mycompany.mytherapy.model.Psychologist;
import com.mycompany.mytherapy.service.PatientService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*") //Allows front-end testing from any origin
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/psychologist/{psychologistId}")
    public List<Patient> listByPsychologist(@PathVariable Long psychologistId) {
        return patientService.listByPsychologist(psychologistId);
    }

    @PostMapping
    public Patient save(@RequestBody Patient patient) { //Add a new patient
        return patientService.save(patient);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { //Delete a patient
        patientService.delete(id);
    }

    @GetMapping
    public List<Patient> listAll() { //List all patients
        return patientService.listAll();
    }

    @GetMapping("/{id}")
    public Patient getById(@PathVariable Long id) { // Get single patient by ID
        return patientService.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @RequestBody Patient patient) { // Update patient
        patient.setId(id); // Ensure the ID matches the path
        return patientService.save(patient);
    }
}
