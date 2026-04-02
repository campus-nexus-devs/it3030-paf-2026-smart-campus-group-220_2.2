package com.smartcampus.it3030_paf_2026_smart_campus.resource;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Resource create(@Valid @RequestBody Resource resource) {
        return resourceService.create(resource);
    }

    @GetMapping
    public List<Resource> getAllOrSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ResourceType type,
            @RequestParam(required = false) ResourceCategory category,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity,
            @RequestParam(required = false) Boolean wifiAvailable,
            @RequestParam(required = false) Boolean acAvailable
    ) {
        return resourceService.search(name, type, category, minCapacity, maxCapacity, wifiAvailable, acAvailable);
    }

    @GetMapping("/{id}")
    public Resource getById(@PathVariable Long id) {
        return resourceService.getById(id);
    }

    @PutMapping("/{id}")
    public Resource update(@PathVariable Long id, @Valid @RequestBody Resource resource) {
        return resourceService.update(id, resource);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        resourceService.delete(id);
    }
}
