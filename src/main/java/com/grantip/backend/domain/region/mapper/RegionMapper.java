package com.grantip.backend.domain.region.mapper;

import com.grantip.backend.domain.region.domain.dto.response.RegionResponse;
import com.grantip.backend.domain.region.domain.entity.Region;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegionMapper {
  RegionResponse toResponse(Region region);
}
