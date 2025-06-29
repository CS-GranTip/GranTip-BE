package com.grantip.backend.domain.scholarship.controller;

import com.grantip.backend.domain.scholarship.service.ScholarshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScholarshipController {
    private final ScholarshipService scholarshipService;
}
