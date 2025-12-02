package com.mycompany.mytherapy.repository;

import com.mycompany.mytherapy.model.Psychologist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PsychologistRepo extends JpaRepository<Psychologist, Long> {

    Optional<Psychologist> findByEmail(String email);

    boolean existsByEmail(String email);

    public List<Psychologist> findBySpecialtyContainingIgnoreCase(String specialty);

    @Query("SELECT DISTINCT p.specialty FROM Psychologist p WHERE p.specialty IS NOT NULL")
    List<String> findDistinctSpecialties();
}
