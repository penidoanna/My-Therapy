package com.mycompany.mytherapy.controller;

import com.mycompany.mytherapy.model.Psychologist;
import com.mycompany.mytherapy.service.PsychologistService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/psychologists")
@CrossOrigin(origins = "*")

public class PsychologistController {

    @Autowired
    private PsychologistService psychologistService;

    @GetMapping
    public List<Psychologist> listAll() { //Get all psychologists
        return psychologistService.listAll();
    }

    @PostMapping("/register")
    public Psychologist register(@RequestBody Psychologist psychologist) { //Register a new psychologist
        return psychologistService.register(psychologist);
    }

    @PostMapping("/login")
    public Optional<Psychologist> login(@RequestParam String email, @RequestParam String password) {
        return psychologistService.login(email, password);
    }

    @GetMapping("/check-email")
    public boolean emailExists(@RequestParam String email) {  //Check if email exists
        return psychologistService.emailExists(email);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Psychologist> updatePsychologist(@PathVariable Long id, @RequestBody Psychologist psychologistDetails) {  //Edit psychologist
        Optional<Psychologist> psychologist = psychologistService.findById(id);
        if (psychologist.isPresent()) {
            Psychologist existingPsychologist = psychologist.get();
            existingPsychologist.setfName(psychologistDetails.getfName());
            existingPsychologist.setlName(psychologistDetails.getlName());
            existingPsychologist.setEmail(psychologistDetails.getEmail());
            existingPsychologist.setPhoneNumber(psychologistDetails.getPhoneNumber());
            existingPsychologist.setSpecialty(psychologistDetails.getSpecialty());
            existingPsychologist.setProfessionalReg(psychologistDetails.getProfessionalReg());
            existingPsychologist.setPassword(psychologistDetails.getPassword());
            
            Psychologist updatedPsychologist = psychologistService.save(existingPsychologist);
            return ResponseEntity.ok(updatedPsychologist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePsychologist(@PathVariable Long id) {  // DELETE psychologist
        if (psychologistService.existsById(id)) {
            psychologistService.deleteById(id);
            return ResponseEntity.ok().build(); // Return 200 OK
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        }
    }
}
