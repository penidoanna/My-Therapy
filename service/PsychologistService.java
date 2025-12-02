package com.mycompany.mytherapy.service;

import com.mycompany.mytherapy.model.Psychologist;
import com.mycompany.mytherapy.repository.PsychologistRepo;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PsychologistService {

    @Autowired
    private PsychologistRepo psychologistRepo;

    public java.util.List<Psychologist> listAll() {
        return psychologistRepo.findAll();
    }

    public Psychologist register(Psychologist psychologist) {
        return psychologistRepo.save(psychologist);
    }

    public Optional<Psychologist> login(String email, String password) {
        return psychologistRepo.findByEmail(email)
                .filter(p -> p.getPassword().equals(password));
    }

    public boolean emailExists(String email) {
        return psychologistRepo.existsByEmail(email);
    }

    public Optional<Psychologist> findById(Long id) {
        return psychologistRepo.findById(id);
    }

    public Psychologist findByEmail(String email) {
        return psychologistRepo.findByEmail(email).orElse(null);
    }

    public Psychologist save(Psychologist psychologist) {
        return psychologistRepo.save(psychologist);
    }

    public boolean existsById(Long id) {
        return psychologistRepo.existsById(id);
    }

    public void deleteById(Long id) {
        psychologistRepo.deleteById(id);
    }

    public List<Psychologist> findBySpecialty(String specialty) {
        return psychologistRepo.findBySpecialtyContainingIgnoreCase(specialty);
    }

    public List<Psychologist> findByNameContaining(String searchTerm) {
        List<Psychologist> allPsychologists = psychologistRepo.findAll();
        String searchLower = searchTerm.toLowerCase().trim();

        return allPsychologists.stream()
                .filter(psychologist -> {
                    String firstName = psychologist.getfName() != null ? psychologist.getfName().toLowerCase() : "";
                    String lastName = psychologist.getlName() != null ? psychologist.getlName().toLowerCase() : "";
                    String specialty = psychologist.getSpecialty() != null ? psychologist.getSpecialty().toLowerCase() : "";
                    String email = psychologist.getEmail() != null ? psychologist.getEmail().toLowerCase() : "";

                    return firstName.contains(searchLower)
                            || lastName.contains(searchLower)
                            || specialty.contains(searchLower)
                            || email.contains(searchLower);
                })
                .collect(Collectors.toList());
    }

    public List<String> findAllSpecialties() {
        return psychologistRepo.findDistinctSpecialties();
    }
}
