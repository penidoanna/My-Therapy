package com.mycompany.mytherapy.service;

import com.mycompany.mytherapy.model.AvailabilitySlot;
import com.mycompany.mytherapy.repository.AvailabilitySlotRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvailabilitySlotService {

    @Autowired
    private AvailabilitySlotRepo availabilitySlotRepo;

    public List<AvailabilitySlot> findByPsychologist(Long psychologistId) {
        List<AvailabilitySlot> slots = availabilitySlotRepo.findByPsychologistId(psychologistId);
        return slots;
    }

    public AvailabilitySlot save(AvailabilitySlot slot) {
        return availabilitySlotRepo.save(slot);
    }

    public void deleteById(Long id) {
        availabilitySlotRepo.deleteById(id);
    }

    public void deleteByPsychologistId(Long id) {
        List<AvailabilitySlot> existingSlots = findByPsychologist(id);
        for (AvailabilitySlot slot : existingSlots) {
            deleteById(slot.getId());
        }
    }
}
