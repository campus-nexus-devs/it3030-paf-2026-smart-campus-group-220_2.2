package com.smartcampus.it3030_paf_2026_smart_campus.resource;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Resource create(Resource resource) {
        resource.setId(null);
        return resourceRepository.save(resource);
    }

    public List<Resource> getAll() {
        return resourceRepository.findAll();
    }

    public Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id " + id));
    }

    public Resource update(Long id, Resource updatedResource) {
        Resource existing = getById(id);
        existing.setName(updatedResource.getName());
        existing.setType(updatedResource.getType());
        existing.setCategory(updatedResource.getCategory());
        existing.setLocation(updatedResource.getLocation());
        existing.setCapacity(updatedResource.getCapacity());
        existing.setWifiAvailable(updatedResource.isWifiAvailable());
        existing.setAcAvailable(updatedResource.isAcAvailable());
        return resourceRepository.save(existing);
    }

    public void delete(Long id) {
        Resource existing = getById(id);
        resourceRepository.delete(existing);
    }

    public List<Resource> search(String name,
                                 ResourceType type,
                                 ResourceCategory category,
                                 Integer minCapacity,
                                 Integer maxCapacity,
                                 Boolean wifiAvailable,
                                 Boolean acAvailable) {
        Specification<Resource> spec = Specification.where(null);

        if (name != null && !name.isBlank()) {
            spec = spec.and(ResourceSpecifications.nameContains(name));
        }
        if (type != null) {
            spec = spec.and(ResourceSpecifications.hasType(type));
        }
        if (category != null) {
            spec = spec.and(ResourceSpecifications.hasCategory(category));
        }
        if (minCapacity != null) {
            spec = spec.and(ResourceSpecifications.capacityGreaterThanOrEqual(minCapacity));
        }
        if (maxCapacity != null) {
            spec = spec.and(ResourceSpecifications.capacityLessThanOrEqual(maxCapacity));
        }
        if (wifiAvailable != null) {
            spec = spec.and(ResourceSpecifications.hasWifi(wifiAvailable));
        }
        if (acAvailable != null) {
            spec = spec.and(ResourceSpecifications.hasAc(acAvailable));
        }

        return resourceRepository.findAll(spec);
    }
}
