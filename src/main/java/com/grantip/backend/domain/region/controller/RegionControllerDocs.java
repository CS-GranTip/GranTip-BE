package com.grantip.backend.domain.region.controller;

import com.grantip.backend.domain.region.domain.dto.response.RegionResponse;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface RegionControllerDocs {
  @Operation(
      summary = "시/도 지역 리스트 조회",
      description = """
            ### 요청 파라미터
            - 없음

            ### 응답 데이터
            - `id` (Long): 지역 ID
            - `regionName` (String): 지역명

            ### 사용 방법
            1. HTTP `GET /api/regions/root` 요청을 보냅니다.
            2. 응답으로 시/도 리스트가 반환됩니다.

            ### 유의 사항
            - 인증이 필요 없습니다.
            - 캐싱 정책에 따라 응답이 지연될 수 있습니다.

            ### 예외 처리
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            """
  )
  ResponseEntity<ApiResponse<List<RegionResponse>>> getRootRegions();

  @Operation(
      summary = "하위 지역 리스트 조회",
      description = """
            ### 요청 파라미터
            - `parentId` (Long, required): 상위 지역 ID

            ### 응답 데이터
            - `id` (Long): 하위 지역 ID
            - `regionName` (String): 하위 지역명

            ### 사용 방법
            1. HTTP `GET /api/regions/{parentId}/children` 요청을 보냅니다.
            2. 경로 변수 `{parentId}`에 상위 지역 ID를 전달합니다.
            3. 응답으로 하위 지역 리스트가 반환됩니다.

            ### 유의 사항
            - `{parentId}`에 해당하는 상위 지역이 존재해야 합니다.

            ### 예외 처리
            - `BAD_REQUEST` (400): `parentId`가 유효하지 않을 때 반환됩니다.
            - `NOT_FOUND` (404): 상위 지역을 찾을 수 없을 때 반환됩니다.
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            """
  )
  ResponseEntity<ApiResponse<List<RegionResponse>>> getChildRegions(
      Long parentId
  );
}
