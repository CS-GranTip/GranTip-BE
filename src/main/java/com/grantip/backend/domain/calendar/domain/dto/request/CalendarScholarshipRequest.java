package com.grantip.backend.domain.calendar.domain.dto.request;

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
public class CalendarScholarshipRequest {
  private LocalDate startDate;

  private LocalDate endDate;
}
