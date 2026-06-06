package com.avijitnath.url_shortener.exception;

public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String shortCode) {
        super("URL got expired for short code: " + shortCode);
    }
}
