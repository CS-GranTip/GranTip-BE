package com.grantip.backend.global.util.database;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageableUtil {
  private static final int MAX_PAGE_SIZE = 100;

  /**
   * 사용자 입력 페이지 번호(1부터 시작)를 인덱스 기반으로 변환합니다.
   */
  public int convertToPageIndex(Integer pageNumber) {
    if (pageNumber == null || pageNumber < 1) {
      return 0;
    }
    return pageNumber - 1;
  }

  /**
   * 페이지 크기를 검증하고 기본값 또는 최대값으로 조정합니다.
   */
  public int validatePageSize(Integer pageSize, Integer defaultPageSize) {
    if (pageSize == null || pageSize < 1) {
      return defaultPageSize;
    }
    return Math.min(pageSize, MAX_PAGE_SIZE);
  }

  /**
   * Pageable 객체를 생성합니다.
   * 사용자 입력 페이지 번호를 인덱스로 변환하고, 페이지 크기를 검증합니다.
   */
  public Pageable createPageable(
      Integer pageNumber,
      Integer pageSize,
      Integer defaultPageSize,
      String sortField,
      Sort.Direction sortDirection) {

    int pageIndex = convertToPageIndex(pageNumber);
    int validatedSize = validatePageSize(pageSize, defaultPageSize);

    if (sortField == null || sortDirection == null) {
      return PageRequest.of(pageIndex, validatedSize, Sort.unsorted());
    }

    Sort sort = Sort.by(sortDirection, sortField);
    return PageRequest.of(pageIndex, validatedSize, sort);
  }
}