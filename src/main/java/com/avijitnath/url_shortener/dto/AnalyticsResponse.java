package com.avijitnath.url_shortener.dto;

import java.util.Map;

public class AnalyticsResponse {

    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private Map<String,Long> clicksPerDay;

    public AnalyticsResponse() {
    }

    public AnalyticsResponse(String shortCode, String originalUrl, Long totalClicks,
                             Map<String, Long> clicksPerDay) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.totalClicks = totalClicks;
        this.clicksPerDay = clicksPerDay;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public Map<String, Long> getClicksPerDay() {
        return clicksPerDay;
    }

    public void setClicksPerDay(Map<String, Long> clicksPerDay) {
        this.clicksPerDay = clicksPerDay;
    }
}
