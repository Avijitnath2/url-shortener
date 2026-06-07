package com.avijitnath.url_shortener.service;

import com.avijitnath.url_shortener.dto.ShortenRequest;
import com.avijitnath.url_shortener.dto.ShortenResponse;
import com.avijitnath.url_shortener.dto.UrlMetadataDto;
import com.avijitnath.url_shortener.entity.ShortUrl;
import com.avijitnath.url_shortener.exception.UrlExpiredException;
import com.avijitnath.url_shortener.exception.UrlNotFoundException;
import com.avijitnath.url_shortener.repository.UrlRepository;
import com.avijitnath.url_shortener.util.Base62Encoder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    private UrlRepository urlRepository;
    private RedisTemplate<String,String> template;

    @Value("${app.redis.ttl}")
    private long ttl;

    @Value("${app.base-url}")
    private String baseUrl;


    @Autowired
    public UrlService(UrlRepository urlRepository, RedisTemplate<String,String> template) {
        this.urlRepository = urlRepository;
        this.template = template;
    }


    @Transactional
    public ShortenResponse shorten(ShortenRequest request){
        ShortUrl url = new ShortUrl();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setCustomAlias(request.getCustomAlias());
        url.setExpiresAt(request.getExpiresAt());

        ShortUrl newUrl= urlRepository.save(url);
        String shortCode;

        if(request.getCustomAlias() != null && !request.getCustomAlias().isBlank()){
            shortCode = request.getCustomAlias();
        }
        else {
            shortCode = Base62Encoder.encode(newUrl.getId());
        }

        newUrl.setShortCode(shortCode);

        urlRepository.save(newUrl);

        ShortenResponse response = new ShortenResponse();
        response.setShortCode(newUrl.getShortCode());
        response.setShortUrl(baseUrl + "/" + newUrl.getShortCode());
        response.setOriginalUrl(newUrl.getOriginalUrl());
        response.setCustomAlias(newUrl.getCustomAlias());
        response.setCreatedAt(newUrl.getCreatedAt());
        response.setExpiresAt(newUrl.getExpiresAt());

        return response;
    }

    //return original url based on the short code/url provided
    public String redirect(String shortCode){
        String cachedUrl = template.opsForValue().get("url:"+shortCode);

        if(cachedUrl != null) {
            return cachedUrl;
        }

        ShortUrl url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        if(url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new UrlExpiredException(shortCode);
        }

        template.opsForValue().set("url:"+shortCode, url.getOriginalUrl(),ttl, TimeUnit.SECONDS);

        return url.getOriginalUrl();
    }


    @Transactional
    public void deleteUrl(String shortCode){
        ShortUrl url = urlRepository.findByShortCode(shortCode).orElseThrow(
                () -> new UrlNotFoundException(shortCode));
        template.delete("url:" + shortCode);
        urlRepository.delete(url);
    }

    public UrlMetadataDto findMetadataOfUrl(String shortCode){
        UrlMetadataDto metaData = new UrlMetadataDto();
        ShortUrl url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
        metaData.setOriginalUrl(url.getOriginalUrl());
        metaData.setCustomAlias(url.getCustomAlias());
        metaData.setCreatedAt(url.getCreatedAt());
        metaData.setExpiresAt(url.getExpiresAt());

        return metaData;
    }

}
