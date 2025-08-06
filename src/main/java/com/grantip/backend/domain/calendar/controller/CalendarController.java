package com.grantip.backend.domain.calendar.controller;

import com.grantip.backend.domain.calendar.domain.dto.request.CalendarScholarshipRequest;
import com.grantip.backend.domain.calendar.domain.dto.response.CalendarScholarshipResponse;
import com.grantip.backend.domain.calendar.service.CalendarService;
import com.grantip.backend.domain.user.service.UserService;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
    name = "캘린더 API",
    description = "캘린더 관련 API 제공"
)
public class CalendarController implements CalendarControllerDocs {
    private final CalendarService calenderService;
    private final UserService userService;

    @Override
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
