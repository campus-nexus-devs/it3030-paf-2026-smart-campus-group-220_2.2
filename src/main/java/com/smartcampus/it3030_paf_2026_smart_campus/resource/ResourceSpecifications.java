package com.smartcampus.it3030_paf_2026_smart_campus.resource;

import org.springframework.data.jpa.domain.Specification;

public final class ResourceSpecifications {

    private ResourceSpecifications() {
    }

    public static Specification<Resource> nameContains(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Resource> hasType(ResourceType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Resource> capacityGreaterThanOrEqual(Integer minCapacity) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity);
    }

    public static Specification<Resource> capacityLessThanOrEqual(Integer maxCapacity) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("capacity"), maxCapacity);
    }

    public static Specification<Resource> hasWifi(Boolean wifiAvailable) {
        return (root, query, cb) -> cb.equal(root.get("wifiAvailable"), wifiAvailable);
    }

    public static Specification<Resource> hasAc(Boolean acAvailable) {
        return (root, query, cb) -> cb.equal(root.get("acAvailable"), acAvailable);
    }
}
