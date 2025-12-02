package com.mycompany.mytherapy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.mycompany.mytherapy.model.Appointment;
import com.mycompany.mytherapy.model.Psychologist;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByPsychologistId(Long psychologistId);

    List<Appointment> findByPsychologistIdAndDateHourBetween(Long psychologistId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.psychologist.id = :psychologistId "
            + "AND ((a.dateHour BETWEEN :startTime AND :endTime) OR "
            + "(a.endTime BETWEEN :startTime AND :endTime))")
    List<Appointment> findConflictingAppointments(@Param("psychologistId") Long psychologistId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
