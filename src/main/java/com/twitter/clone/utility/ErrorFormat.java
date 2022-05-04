package com.twitter.clone.utility;

import java.time.LocalDateTime;

public class ErrorFormat {
    private String message;
    private int statusCode;
    private LocalDateTime timeStamp;
    ErrorFormat(){}

    ErrorFormat(String message,int statusCode,LocalDateTime timeStamp){
        this.message = message;
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "message='" + message + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
