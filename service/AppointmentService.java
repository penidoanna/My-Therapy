package com.mycompany.mytherapy.service;

import com.mycompany.mytherapy.model.Appointment;
import com.mycompany.mytherapy.model.Psychologist;
import com.mycompany.mytherapy.repository.AppointmentRepo;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepo appointmentRepo;

    public List<Appointment> listAll() {
        return appointmentRepo.findAll();
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepo.findById(id);
    }

    public List<Appointment> listByPatientId(Long patientId) {
        return appointmentRepo.findByPatientId(patientId);
    }

    public List<Appointment> listByPsychologistId(Long psychologistId) {
        return appointmentRepo.findByPsychologistId(psychologistId);
    }

    public Appointment save(Appointment appointment) {
        return appointmentRepo.save(appointment);
    }

    public void delete(Long id) {
        appointmentRepo.deleteById(id);
    }
    
    public void deleteById(Long id) {
        appointmentRepo.deleteById(id);
    }

    public List<LocalDateTime> getAvailableSlots(Long psychologistId, String date) {
        return List.of();
    }

    public boolean isSlotAvailable(Long psychologistId, LocalDateTime proposedStartTime) {
        LocalDateTime startOfDay = proposedStartTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = proposedStartTime.toLocalDate().atTime(LocalTime.MAX);

        List<Appointment> existingAppointments = appointmentRepo.findByPsychologistIdAndDateHourBetween(
                psychologistId, startOfDay, endOfDay);

        LocalDateTime proposedEndTime = proposedStartTime.plusMinutes(60);

        for (Appointment existing : existingAppointments) {
            LocalDateTime existingStart = existing.getDateHour();
            LocalDateTime existingEnd = existing.getEndTime();

            if (existingEnd == null) {
                existingEnd = existingStart.plusMinutes(
                        existing.getDurationMinutes() != null ? existing.getDurationMinutes() : 60);
            }

            // Check if proposed appointment overlaps with existing appointment
            boolean overlaps = proposedStartTime.isBefore(existingEnd) && proposedEndTime.isAfter(existingStart);

            if (overlaps) {
                return false; // Slot not available
            }
        }
        return true; // Slot available
    }

    public boolean hasConflictingAppointment(Psychologist psychologist, LocalDateTime startTime, LocalDateTime endTime) {
        List<Appointment> conflicts = appointmentRepo.findConflictingAppointments(
                psychologist.getId(), startTime, endTime);
        return !conflicts.isEmpty();
    }
}
