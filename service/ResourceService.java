package com.mycompany.mytherapy.service;

import com.mycompany.mytherapy.model.Resource;
import com.mycompany.mytherapy.repository.ResourceRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {

    private final ResourceRepo resourceRepo;

    @Autowired
    public ResourceService(ResourceRepo resourceRepo) {
        this.resourceRepo = resourceRepo;
    }
    
    public List<Resource> getByType(String type) {
        return resourceRepo.findByType(type);
    }

    public List<Resource> getByTypeAndSubject(String type, String subject) {
        return resourceRepo.findByTypeAndSubject(type, subject);
    }

    public Resource addResource(Resource resource) {
        return resourceRepo.save(resource);
    }
    
    public List<Resource> listAll() {
        return resourceRepo.findAll();
    }

    public Resource findById(Long id) {
        return resourceRepo.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        resourceRepo.deleteById(id);
    }

    public Resource save(Resource resource) {
        return resourceRepo.save(resource);
    }
}
