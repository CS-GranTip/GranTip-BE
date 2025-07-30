package com.grantip.backend.domain.region.repository;

import com.grantip.backend.domain.region.domain.entity.Region;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
  List<Region> findByRegionLevel(int regionLevel);

  List<Region> findByParentId(Long parentId);
}
