package com.smartcampus.it3030_paf_2026_smart_campus.service;

import com.smartcampus.it3030_paf_2026_smart_campus.repository.ResourceRepository;
import com.smartcampus.it3030_paf_2026_smart_campus.entity.Resource;
import com.smartcampus.it3030_paf_2026_smart_campus.enums.ResourceCategory;
import com.smartcampus.it3030_paf_2026_smart_campus.enums.ResourceType;
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
        Specification<Resource> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (category != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (minCapacity != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity));
        }
        if (maxCapacity != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("capacity"), maxCapacity));
        }
        if (wifiAvailable != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("wifiAvailable"), wifiAvailable));
        }
        if (acAvailable != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("acAvailable"), acAvailable));
        }

        return resourceRepository.findAll(spec);
    }
}
