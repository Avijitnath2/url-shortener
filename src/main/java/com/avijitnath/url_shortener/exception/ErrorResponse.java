package com.avijitnath.url_shortener.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String errorMessage;
    private LocalDateTime timeStamp;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String errorMessage, LocalDateTime timeStamp) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
