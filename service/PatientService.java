package com.mycompany.mytherapy.service;

import com.mycompany.mytherapy.model.Patient;
import com.mycompany.mytherapy.model.Psychologist;
import com.mycompany.mytherapy.repository.PatientRepo;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepo patientRepo;

    public List<Patient> listByPsychologist(Long psychologistId) {
        return patientRepo.findByPsychologistId(psychologistId);
    }

    public Patient save(Patient patient) {  //Save new or update existing patient
        return patientRepo.save(patient);
    }

    public void delete(Long id) {  //Delete patient by Id
        patientRepo.deleteById(id);
    }

    public List<Patient> listAll() {
        return patientRepo.findAll();
    }

    public Patient findByEmail(String email) {
        return patientRepo.findByEmail(email);
    }

    public Optional<Patient> findById(Long id) {
        return patientRepo.findById(id);
    }

    public boolean emailExists(String email) {
        return patientRepo.findByEmail(email) != null;
    }

    public List<Patient> findByPsychologist(Psychologist psychologist) {
        return patientRepo.findByPsychologist(psychologist);
    }

    public void updateStripeCustomerId(Patient patient, String stripeCustomerId) {
        patient.setStripeCustomerId(stripeCustomerId);
        patientRepo.save(patient);
    }
}
