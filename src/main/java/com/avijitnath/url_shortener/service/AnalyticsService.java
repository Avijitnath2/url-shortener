package com.avijitnath.url_shortener.service;

import com.avijitnath.url_shortener.dto.AnalyticsResponse;
import com.avijitnath.url_shortener.entity.ClickEvent;
import com.avijitnath.url_shortener.entity.ShortUrl;
import com.avijitnath.url_shortener.exception.UrlNotFoundException;
import com.avijitnath.url_shortener.repository.ClickEventRepository;
import com.avijitnath.url_shortener.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private ClickEventRepository clickEventRepository;
    private UrlRepository urlRepository;

    @Autowired
    public AnalyticsService(ClickEventRepository clickEventRepository, UrlRepository urlRepository) {
        this.clickEventRepository = clickEventRepository;
        this.urlRepository = urlRepository;
    }

    @Async
    @Transactional
    public void recordClick(ShortUrl shortUrl, HttpServletRequest request){
        ClickEvent event = new ClickEvent();
        event.setShortUrl(shortUrl);
        event.setIpAddress(request.getRemoteAddr());
        event.setReferrer(request.getHeader("Referer"));
        event.setUserAgent(request.getHeader("User-Agent"));

        clickEventRepository.save(event);
    }

    public AnalyticsResponse getStats(String shortCode){
        AnalyticsResponse response = new AnalyticsResponse();

        ShortUrl shortUrl = urlRepository.findByShortCode(shortCode)
                .orElseThrow(()-> new UrlNotFoundException(shortCode));

        List<ClickEvent> clickEvents = clickEventRepository.findByShortUrlOrderByClickedAtDesc(shortUrl);

        response.setShortCode(shortUrl.getShortCode());
        response.setOriginalUrl(shortUrl.getOriginalUrl());
        response.setTotalClicks(shortUrl.getTotalClicks());

        Map<String,Long> clicksPerDay = clickEvents.stream()
                .collect(Collectors.groupingBy(
                        event-> event.getClickedAt().toLocalDate().toString(),
                        Collectors.counting()
                ));

        response.setClicksPerDay(clicksPerDay);

        return response;
    }

}
