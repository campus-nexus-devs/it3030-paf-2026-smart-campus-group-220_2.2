package com.smartcampus.it3030_paf_2026_smart_campus.resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ResourceCategoryBackfill implements ApplicationRunner {

    private final ResourceRepository resourceRepository;

    public ResourceCategoryBackfill(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (Resource resource : resourceRepository.findAll()) {
            if (resource.getCategory() != null) {
                continue;
            }
            ResourceCategory inferred = inferFromType(resource.getType());
            if (inferred != null) {
                resource.setCategory(inferred);
                resourceRepository.save(resource);
            }
        }
    }

    private static ResourceCategory inferFromType(ResourceType type) {
        if (type == ResourceType.PROJECTOR) {
            return ResourceCategory.PROJECTOR_EQUIPMENT;
        }
        if (type == ResourceType.HALL || type == ResourceType.LAB) {
            return ResourceCategory.HALL_LAB;
        }
        return null;
    }
}
