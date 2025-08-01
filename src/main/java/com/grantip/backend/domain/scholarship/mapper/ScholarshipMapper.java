package com.grantip.backend.domain.scholarship.mapper;

import com.grantip.backend.domain.scholarship.domain.dto.response.RecommendedScholarshipResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipDetailResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import com.grantip.backend.domain.scholarship.domain.entity.UniversityCategory;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ScholarshipMapper {

  ScholarshipSummaryResponse toSummaryResponse(Scholarship scholarship);

  @Mapping(target = ".", source = "scholarship")
  @Mapping(source = "scholarship.universityCategories", target = "universityCategories", qualifiedByName = "mapCategoriesToStrings")
  @Mapping(source = "scholarship.gradeCriteriaDetail", target = "gradeCriteriaDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "scholarship.incomeCriteriaDetail", target = "incomeCriteriaDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "scholarship.supportDetail", target = "supportDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "scholarship.specificQualificationDetail", target = "specificQualificationDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "scholarship.regionResidenceDetail", target = "regionResidenceDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "scholarship.selectionMethodDetail", target = "selectionMethodDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "scholarship.selectionPersonnelDetail", target = "selectionPersonnelDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "scholarship.qualificationRestrictionDetail", target = "qualificationRestrictionDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "scholarship.recommendationNeededDetail", target = "recommendationNeededDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "scholarship.requiredDocumentsDetail", target = "requiredDocumentsDetail", qualifiedByName = "splitDetail")
  @Mapping(target = "score", source = "score")
  RecommendedScholarshipResponse toRecommendedResponse(Scholarship scholarship, double score);

  @Mapping(source = "universityCategories", target = "universityCategories", qualifiedByName = "mapCategoriesToStrings")
  @Mapping(source = "gradeCriteriaDetail", target = "gradeCriteriaDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "incomeCriteriaDetail", target = "incomeCriteriaDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "supportDetail", target = "supportDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "specificQualificationDetail", target = "specificQualificationDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "regionResidenceDetail", target = "regionResidenceDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "selectionMethodDetail", target = "selectionMethodDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "selectionPersonnelDetail", target = "selectionPersonnelDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "qualificationRestrictionDetail", target = "qualificationRestrictionDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "recommendationNeededDetail", target = "recommendationNeededDetail", qualifiedByName = "splitDetail")
  @Mapping(source = "requiredDocumentsDetail", target = "requiredDocumentsDetail", qualifiedByName = "splitDetail")
  ScholarshipDetailResponse toDetailResponse(Scholarship scholarship);

  @Named("mapCategoriesToStrings")
  default List<String> mapCategoriesToStrings(Set<UniversityCategory> categories){
    if(categories == null || categories.isEmpty()){
      return List.of();
    }
    return categories.stream()
        .map(UniversityCategory::getName)
        .collect(Collectors.toList());
  }

  @Named("splitDetail")
  default List<String> splitDetail(String detail){
    if(detail == null || detail.isBlank()){
      return List.of();
    }
    return Arrays.stream(detail.split("○ "))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();
  }
}
