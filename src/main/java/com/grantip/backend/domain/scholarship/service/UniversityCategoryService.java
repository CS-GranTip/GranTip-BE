package com.grantip.backend.domain.scholarship.service;

import com.grantip.backend.domain.scholarship.domain.entity.UniversityCategory;
import com.grantip.backend.domain.scholarship.repository.UniversityCategoryRepository;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UniversityCategoryService {
  private final UniversityCategoryRepository universityCategoryRepository;

  public UniversityCategory findById(Long id){
    return universityCategoryRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.UNIVERSITY_CATEGORY_NOT_FOUND));
  }
}
