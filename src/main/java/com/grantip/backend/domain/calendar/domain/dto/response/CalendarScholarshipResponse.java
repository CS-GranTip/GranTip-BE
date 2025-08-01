package com.grantip.backend.domain.calendar.domain.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarScholarshipResponse {
  private Long scholarshipId;

  private String productName;

  private LocalDate applicationStartDate;

  private LocalDate applicationEndDate;
}
