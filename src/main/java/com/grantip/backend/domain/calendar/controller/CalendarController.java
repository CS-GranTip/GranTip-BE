package com.grantip.backend.domain.calendar.controller;

import com.grantip.backend.domain.calendar.domain.dto.request.CalendarScholarshipRequest;
import com.grantip.backend.domain.calendar.domain.dto.response.CalendarScholarshipResponse;
import com.grantip.backend.domain.calendar.service.CalendarService;
import com.grantip.backend.domain.user.service.UserService;
import com.grantip.backend.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendars")
public class CalendarController {
    private final CalendarService calenderService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CalendarScholarshipResponse>>> getCalendar(
        @AuthenticationPrincipal UserDetails userDetails,
        @ParameterObject CalendarScholarshipRequest request){
      return ResponseEntity.ok(
          ApiResponse
              .<List<CalendarScholarshipResponse>>builder()
              .success(true)
              .code(200)
              .result(calenderService.getCalendar(userService.findByEmail(userDetails.getUsername()), request))
              .message("캘린더 조회에 성공했습니다.")
              .build()
      );
    }
}
