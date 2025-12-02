package com.mycompany.mytherapy.security;

import com.mycompany.mytherapy.model.Psychologist;
import com.mycompany.mytherapy.repository.PsychologistRepo;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class PsychologistUserDetailsService implements UserDetailsService {

    @Autowired
    private PsychologistRepo psychologistRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Psychologist psychologist = psychologistRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Psychologist not found"));

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_PSYCHOLOGIST")
        );

        return new User(
            psychologist.getEmail(),
            psychologist.getPassword(),
            authorities
        );
    }
}
