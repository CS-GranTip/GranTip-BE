package com.grantip.backend.domain.calendar.repository;

import com.grantip.backend.domain.calendar.domain.dto.response.CalendarScholarshipResponse;
import com.grantip.backend.domain.user.domain.entity.User;
import java.time.LocalDate;
import java.util.List;

public interface CalendarRepositoryCustom {
  List<CalendarScholarshipResponse> findFavoriteScholarshipsInPeriod(User user, LocalDate startDate, LocalDate endDate);
}
