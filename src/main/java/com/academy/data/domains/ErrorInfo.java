package com.academy.data.domains;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorInfo {

    private String url;
    private String message;

    public ErrorInfo(String url, String message) {
        this.url = url;
        this.message = message;
    }
}
