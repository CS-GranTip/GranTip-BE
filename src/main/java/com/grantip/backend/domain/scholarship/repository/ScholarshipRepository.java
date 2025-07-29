package com.grantip.backend.domain.scholarship.repository;

import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {

}
