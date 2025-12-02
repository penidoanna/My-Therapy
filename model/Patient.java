package com.mycompany.mytherapy.model;

import jakarta.persistence.*;
import java.util.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "patients")

public class Patient {

    //Variables
    @NotBlank(message = "First name is required")
    @Column(name = "f_name")
    private String fName;

    @NotBlank(message = "Last name is required")
    @Column(name = "l_name")
    private String lName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "country")
    private String country;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Column(name = "email")
    private String email;

    @Column(name = "notes")
    private String notes;

    @Column(name = "password")
    private String password;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "psychologist_id", nullable = true) //Foreign key on patient's table
    private Psychologist psychologist;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointmentHistory;

    //Empty Constructor for JPA
    public Patient() {
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }
    
    public Psychologist getPsychologist() {
        return psychologist;
    }
    
    public void setPsychologist(Psychologist psychologist) {
        this.psychologist = psychologist;
    }

    //Constructor
    public Patient(Long id, String fName, String lName, String phone, String country, String email, String notes) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.country = country;
        this.email = email;
        this.notes = notes;
    }

    //Constructor
    public Patient(Long id, String fName, String lName, String phone, String country, String email, String notes, Psychologist psychologist, List<Appointment> appointmentHistory) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.country = country;
        this.email = email;
        this.notes = notes;
        this.psychologist = psychologist;
        this.appointmentHistory = appointmentHistory;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Patient{"
                + "id=" + id
                + ", First name = " + fName
                + ", Last name = " + lName
                + ", phone=" + phone
                + ", country=" + country
                + ", email=" + email
                + ", notes=" + notes + '}';
    }

}
