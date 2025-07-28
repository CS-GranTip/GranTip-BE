package com.grantip.backend.domain.scholarship.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ProviderType {
  LOCAL_GOV("지자체(출자출연기관)"),
  PUBLIC_ORG("공공기관"),
  ETC("기타");

  private final String description;

  @JsonValue
  public String getDescription(){
    return description;
  }

  @JsonCreator
  public static ProviderType fromDescription(String description){
    for(ProviderType type : ProviderType.values()){
      if(type.getDescription().equals(description)){
        return type;
      }
    }
    return null;
  }
}

