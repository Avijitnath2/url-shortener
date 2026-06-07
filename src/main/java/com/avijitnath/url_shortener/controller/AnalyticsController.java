package com.avijitnath.url_shortener.controller;

import com.avijitnath.url_shortener.dto.AnalyticsResponse;
import com.avijitnath.url_shortener.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    private AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/api/stats/{code}")
    public ResponseEntity<AnalyticsResponse> getStats(@PathVariable String code){

        return new ResponseEntity<>(analyticsService.getStats(code), HttpStatus.OK);

    }


}
