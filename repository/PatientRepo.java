package com.mycompany.mytherapy.repository;

import com.mycompany.mytherapy.model.Patient;
import com.mycompany.mytherapy.model.Psychologist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepo extends JpaRepository<Patient, Long> {
    
    List<Patient> findByPsychologistId(Long psychologistId);
    
    Patient findByEmail(String email);

    public List<Patient> findByPsychologist(Psychologist psychologist);
}
