package com.grantip.backend.domain.user.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UnivYear {
  FRESHMAN("대학신입생"),
  SECOND_SEMESTER("대학2학기"),
  THIRD_SEMESTER("대학3학기"),
  FOURTH_SEMESTER("대학4학기"),
  FIFTH_SEMESTER("대학5학기"),
  SIXTH_SEMESTER("대학6학기"),
  SEVENTH_SEMESTER("대학7학기"),
  EIGHTH_SEMESTER_OR_ABOVE("대학8학기이상");

  private final String description;

  @JsonValue
  public String getDescription() {
    return description;
  }

  @JsonCreator
  public static UnivYear fromValue(String value) {
    for (UnivYear year : UnivYear.values()) {
      if (year.name().equalsIgnoreCase(value)) {
        return year;
      }
    }
    throw new CustomException(ErrorCode.INVALID_UNIV_YEAR);
  }
}
