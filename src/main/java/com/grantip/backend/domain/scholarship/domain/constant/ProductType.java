package com.grantip.backend.domain.scholarship.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ProductType {
  SCHOLARSHIP("장학금"),
  LOAN("학자금");

  private final String description;

  @JsonValue
  public String getDescription(){
    return description;
  }

  @JsonCreator
  public static ProductType fromDescription(String description){
    for(ProductType type : ProductType.values()){
      if(type.getDescription().equals(description)){
        return type;
      }
    }
    throw new CustomException(ErrorCode.INVALID_PRODUCT_TYPE);
  }
}
