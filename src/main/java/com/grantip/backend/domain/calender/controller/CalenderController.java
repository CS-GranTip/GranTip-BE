package com.grantip.backend.domain.calender.controller;

import com.grantip.backend.domain.calender.service.CalenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CalenderController {
    private final CalenderService calenderService;
}
