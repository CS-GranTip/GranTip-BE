package com.grantip.backend.domain.region.service;

import com.grantip.backend.domain.region.domain.dto.response.RegionResponse;
import com.grantip.backend.domain.region.mapper.RegionMapper;
import com.grantip.backend.domain.region.repository.RegionRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegionService {
  private final RegionRepository regionRepository;
  private final RegionMapper regionMapper;

  // root 지역 리스트 조회
  @Transactional(readOnly = true)
  public List<RegionResponse> getRootRegions(){
    return regionRepository.findByRegionLevel(1).stream()
        .map(regionMapper::toResponse)
        .collect(Collectors.toList());
  }

  // child 지역 리스트 조회
  @Transactional(readOnly = true)
  public List<RegionResponse> getChildRegions(Long parentId){
    return regionRepository.findByParentId(parentId).stream()
        .map(regionMapper::toResponse)
        .collect(Collectors.toList());
  }
}
