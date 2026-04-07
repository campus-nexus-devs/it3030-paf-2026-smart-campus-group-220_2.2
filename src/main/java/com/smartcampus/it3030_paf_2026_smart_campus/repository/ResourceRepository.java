package com.smartcampus.it3030_paf_2026_smart_campus.repository;

import com.smartcampus.it3030_paf_2026_smart_campus.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {
}
