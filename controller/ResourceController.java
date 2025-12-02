package com.mycompany.mytherapy.controller;

import com.mycompany.mytherapy.model.Resource;
import com.mycompany.mytherapy.service.ResourceService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/{type}")
    public List<Resource> getByType(@PathVariable String type) {
        return resourceService.getByType(type);
    }

    @GetMapping("/{type}/{subject}")
    public List<Resource> getByTypeAndSubject(@PathVariable String type, @PathVariable String subject) {
        return resourceService.getByTypeAndSubject(type, subject);
    }

    @PostMapping
    public Resource create(@RequestBody Resource resource) {
        return resourceService.addResource(resource);
    }
}