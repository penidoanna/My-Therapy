package com.mycompany.mytherapy.security;

import com.mycompany.mytherapy.model.Patient;
import com.mycompany.mytherapy.repository.PatientRepo;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
public class PatientUserDetailsService implements UserDetailsService {

    private final PatientRepo patientRepo;

    public PatientUserDetailsService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // ✔ Correct use of Optional
        Patient patient = patientRepo.findByEmail(email);
             if (patient == null) { throw new UsernameNotFoundException("Patient not found"); }

        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_PATIENT")
        );

        return new User(
            patient.getEmail(),
            patient.getPassword(),
            authorities
        );
    }
}
