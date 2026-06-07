package com.avijitnath.url_shortener.controller;

import com.avijitnath.url_shortener.dto.ShortenRequest;
import com.avijitnath.url_shortener.dto.ShortenResponse;
import com.avijitnath.url_shortener.dto.UrlMetadataDto;
import com.avijitnath.url_shortener.entity.ShortUrl;
import com.avijitnath.url_shortener.service.AnalyticsService;
import com.avijitnath.url_shortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private UrlService urlService;
    private AnalyticsService analyticsService;

    @Autowired
    public UrlController(UrlService urlService, AnalyticsService analyticsService) {
        this.urlService = urlService;
        this.analyticsService = analyticsService;
    }

    // Post mapping to create the short code based on the provided original url
    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request){
        return new ResponseEntity<>(urlService.shorten(request), HttpStatus.CREATED);
    }

    // Get mapping to redirect to original url when valid short code is provided
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code, HttpServletRequest request){
        ShortUrl url = urlService.redirect(code);
        analyticsService.recordClick(url, request);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url.getOriginalUrl()))
                .build();
    }


    @DeleteMapping("/api/urls/{code}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String code){
        urlService.deleteUrl(code);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/urls/{code}")
    public ResponseEntity<UrlMetadataDto> findMetadataOfUrl(@PathVariable String code){
        UrlMetadataDto metaData = urlService.findMetadataOfUrl(code);

        return new ResponseEntity<>(metaData,HttpStatus.OK);
    }

}
