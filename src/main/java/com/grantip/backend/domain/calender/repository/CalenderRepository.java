package com.grantip.backend.domain.calender.repository;

import com.grantip.backend.domain.calender.entity.ScholarCalender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalenderRepository extends JpaRepository<ScholarCalender, Long> {

}
