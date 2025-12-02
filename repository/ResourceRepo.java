package com.mycompany.mytherapy.repository;

import com.mycompany.mytherapy.model.Resource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface ResourceRepo extends JpaRepository<Resource, Long> {
    List<Resource> findByType(String type);
    List<Resource> findByTypeAndSubject(String type, String subject);
}
    
