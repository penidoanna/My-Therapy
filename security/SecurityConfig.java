package com.mycompany.mytherapy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PatientUserDetailsService patientUserDetailsService;

    @Autowired
    private PsychologistUserDetailsService psychologistUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider patientAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(patientUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider psychologistAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(psychologistUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(patientAuthProvider());
        http.authenticationProvider(psychologistAuthProvider());

        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        // Public pages
                        "/",
                        "/register",
                        "/login",
                        "/patients/create",
                        "/webjars/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/psychologists",
                        "/psychologists/create",
                        "/appointments/book",
                        "/api/**"
                ).permitAll()
                // Public availability view - allow everyone
                .requestMatchers("/psychologists/*/availability/view").permitAll()
                // Resources - accessible to both patients and psychologists
                .requestMatchers("/resources", "/resources/**").hasAnyRole("PATIENT", "PSYCHOLOGIST")
                // Patient area
                .requestMatchers("/patient/**", "/appointments").hasRole("PATIENT")
                // Psychologist area
                .requestMatchers("/psychologist/**", "/psychologists/*/availability", "/psychologist/patients/create").hasRole("PSYCHOLOGIST")
                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard-redirect", true)
                .permitAll()
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                )
                .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}
