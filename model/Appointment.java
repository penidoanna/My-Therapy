package com.mycompany.mytherapy.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")

public class Appointment {
       
    //Variables
    @Column(name = "date_hour")
    private LocalDateTime dateHour;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "paid")
    private boolean paid;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id") //Relationship many appointments to one patient
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "psychologist_id") //Relationship many appointments to one psychologist
    private Psychologist psychologist;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes = 60; // Default 60 minutes appointment slot
    
    @Column(name = "end_time")
    private LocalDateTime endTime;

    //Constructor
    public Appointment() {} //Empty constructor for JPA

    //Constructor
    public Appointment(Long id, LocalDateTime dateHour, String notes, Patient patient, Psychologist psychologist, boolean paid) {
        this.id = id;
        this.dateHour = dateHour;
        this.notes = notes;
        this.patient = patient;
        this.psychologist = psychologist;
        this.paid = paid;
        this.durationMinutes = 60;
        this.endTime = calculateEndTime();
    }   
    
    //Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateHour() {
        return dateHour;
    }

    public void setDateHour(LocalDateTime dateHour) {
        this.dateHour = dateHour;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Psychologist getPsychologist() {
        return psychologist;
    }

    public void setPsychologist(Psychologist psychologist) {
        this.psychologist = psychologist;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
        this.endTime = calculateEndTime(); 
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        this.endTime = calculateEndTime(); 
    }
        
    
    @Override
    public String toString() {
        return "Appointment{" + "id=" + id + 
                ", dateHour=" + dateHour + 
                ", notes=" + notes + 
                ", patient=" + (patient != null ? patient.getId() : null) +
                ", psychologist=" + (psychologist != null ? psychologist.getId() : null) + '}';
    }

    private LocalDateTime calculateEndTime() {
        if (this.dateHour != null && this.durationMinutes != null) {
            return this.dateHour.plusMinutes(this.durationMinutes);
        }
        return null;
    }
}
