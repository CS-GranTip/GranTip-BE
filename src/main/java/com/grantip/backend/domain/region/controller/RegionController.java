package com.grantip.backend.domain.region.controller;

import com.grantip.backend.domain.region.domain.dto.response.RegionResponse;
import com.grantip.backend.domain.region.service.RegionService;
import com.grantip.backend.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionController {
  private final RegionService regionService;

  @GetMapping("/root")
  public ResponseEntity<ApiResponse<List<RegionResponse>>> getRootRegions(){
    return ResponseEntity.ok(
        ApiResponse.<List<RegionResponse>>builder()
            .success(true)
            .code(200)
            .result(regionService.getRootRegions())
            .message("시/도 지역 리스트 조회에 성공했습니다.")
            .build());
  }

  @GetMapping("/{parentId}/children")
  public ResponseEntity<ApiResponse<List<RegionResponse>>> getChildRegions(@PathVariable Long parentId){
    return ResponseEntity.ok(
        ApiResponse.<List<RegionResponse>>builder()
            .success(true)
            .code(200)
            .result(regionService.getChildRegions(parentId))
            .message("자녀 지역 리스트 조회에 성공했습니다.")
            .build());
  }
}
