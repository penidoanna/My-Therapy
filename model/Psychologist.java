package com.mycompany.mytherapy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;
import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "psychologists")

public class Psychologist {
     
    //Variables
    
    @NotBlank(message = "First name is required")
    @Column(name = "fname")
    private String fName;
    
    @NotBlank(message = "Last name is required")
    @Column(name = "lname")
    private String lName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @NotBlank(message = "Professional registration number is required")
    @Column(name = "professional_reg")
    private String professionalReg;
    
    @NotBlank(message = "Specialty is required")
    private String specialty;
    
    private String password;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "psychologist", cascade = CascadeType.ALL) //Relationship one psychologist to many patients
    @JsonIgnore  // Prevents patients from appearing in JSON
    private List<Patient> patients;
    
    @OneToMany(mappedBy = "psychologist", cascade = CascadeType.ALL) //Relationship one psychologist to many appointments
    @JsonIgnore  // Prevents appointments from appearing in JSON
    private List<Appointment> appointments;

    //Constructor
    public Psychologist() {} //Empty constructor for JPA   
    
    //Contructor
    public Psychologist(long id, String fName, String lName, String email, String phoneNumber, String professionalReg, String specialty, String password, List<Patient> patients, List<Appointment> appointments) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.professionalReg = professionalReg;
        this.specialty = specialty;
        this.password = password;
        this.patients = patients;
        this.appointments = appointments;
    }
    
    //Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfessionalReg() {
        return professionalReg;
    }

    public void setProfessionalReg(String professionalReg) {
        this.professionalReg = professionalReg;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }  

    @Override
    public String toString() {
        return "Psychologist{" + "id=" + id + 
                ", fName=" + fName + 
                ", lName=" + lName + 
                ", email=" + email + 
                ", phoneNumber=" + phoneNumber + 
                ", professionalReg=" + professionalReg + 
                ", specialty=" + specialty + 
                ", password=" + password + 
                ", patients=" + patients + '}';
    }        
}
