package com.mycompany.mytherapy.controller;

import com.mycompany.mytherapy.model.Appointment;
import com.mycompany.mytherapy.service.AppointmentService;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")

public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/patient/{patientId}")
    public List<Appointment> listByPatient(@PathVariable Long patientId) {
        return appointmentService.listByPatientId(patientId);
    }

    @GetMapping("/psychologist/{psychologistId}")
    public List<Appointment> listByPsychologist(@PathVariable Long psychologistId) {
        return appointmentService.listByPsychologistId(psychologistId);
    }
    
    @GetMapping("/available-slots/{psychologistId}")
    public List<LocalDateTime> getAvailableSlots(
            @PathVariable Long psychologistId,
            @RequestParam String date) { // Expecting format: "2025-01-20"
        
        return appointmentService.getAvailableSlots(psychologistId, date);
    }

    @PostMapping
    public Appointment save(@RequestBody Appointment appointment) { //Create a new appointment
        if (!appointmentService.isSlotAvailable(
            appointment.getPsychologist().getId(), 
            appointment.getDateHour())) {
            throw new RuntimeException("Time slot unavailable. This time conflicts with an existing 60-minute appointment.");
        }
        
        // Set default duration to 60 minutes
        appointment.setDurationMinutes(60);
        
        return appointmentService.save(appointment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        appointmentService.delete(id);
    }

    @GetMapping
    public List<Appointment> listAll() {
        return appointmentService.listAll();
    }

    @GetMapping("/{id}")
    public Appointment getById(@PathVariable Long id) {
        return appointmentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @PutMapping("/{id}")
    public Appointment update(@PathVariable Long id, @RequestBody Appointment appointment) {
        // Ensure appointment exists
        appointmentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setId(id);
        return appointmentService.save(appointment);
    }

}
