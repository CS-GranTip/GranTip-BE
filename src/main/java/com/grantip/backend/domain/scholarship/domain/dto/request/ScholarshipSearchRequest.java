package com.grantip.backend.domain.scholarship.domain.dto.request;

import com.grantip.backend.domain.scholarship.domain.constant.ScholarshipCategory;
import com.grantip.backend.global.constant.PageableConstant;
import com.grantip.backend.global.util.database.PageableUtil;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipSearchRequest {
  @Nullable
  private String keyword;

  @Nullable
  @Schema(type = "string", allowableValues = {"LOCAL", "SPECIALTY", "GRADE", "INCOME", "DISABILITY", "ETC"}, example = "LOCAL")
  private ScholarshipCategory category;

  @Default
  @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
  @Max(value = Integer.MAX_VALUE, message = "정수 최대 범위를 넘을 수 없습니다.")
  private Integer pageNumber = 1;

  @Default
  @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
  @Max(value = PageableConstant.MAX_PAGE_SIZE, message = "페이지 당 데이터 최댓값은 " + PageableConstant.MAX_PAGE_SIZE + "개 입니다.")
  private Integer pageSize = 10;

  public Pageable toPageable(){
    return PageableUtil.createPageable(
        pageNumber,
        pageSize,
        PageableConstant.DEFAULT_PAGE_SIZE,
        "applicationStartDate",
        Direction.DESC
    );
  }
}
