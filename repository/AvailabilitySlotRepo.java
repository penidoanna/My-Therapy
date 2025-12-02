package com.mycompany.mytherapy.repository;

import com.mycompany.mytherapy.model.AvailabilitySlot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AvailabilitySlotRepo extends JpaRepository<AvailabilitySlot, Long> {

    void deleteByPsychologistId(Long id);

    List<AvailabilitySlot> findByPsychologistId(@Param("psychologistId") Long psychologistId);
}
