package com.grantip.backend.domain.calendar.service;

import com.grantip.backend.domain.calendar.domain.dto.request.CalendarScholarshipRequest;
import com.grantip.backend.domain.calendar.domain.dto.response.CalendarScholarshipResponse;
import com.grantip.backend.domain.calendar.repository.CalendarRepositoryCustom;
import com.grantip.backend.domain.user.domain.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarService {
  private final CalendarRepositoryCustom calendarRepositoryCustom;
  public List<CalendarScholarshipResponse> getCalendar(User user, CalendarScholarshipRequest request){
    return calendarRepositoryCustom.findFavoriteScholarshipsInPeriod(user, request.getStartDate(), request.getEndDate());
  }
}
