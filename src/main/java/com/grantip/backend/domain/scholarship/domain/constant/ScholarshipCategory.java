package com.grantip.backend.domain.scholarship.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ScholarshipCategory {
  LOCAL("지역연고"),
  SPECIALTY("특기자"),
  GRADE("성적우수"),
  INCOME("소득구분"),
  DISABILITY("장애인"),
  ETC("기타");

  private final String description;

  @JsonValue
  public String getDescription(){
    return description;
  }

  @JsonCreator
  public static ScholarshipCategory fromDescription(String description){
    for(ScholarshipCategory type : ScholarshipCategory.values()){
      if(type.getDescription().equals(description)){
        return type;
      }
    }
    return null;
  }
}
