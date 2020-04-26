package com.academy.data.rest;

import com.academy.data.domains.ErrorInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Locale;

@ControllerAdvice
public class RestControllerAdvice {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private RestControllerAdvice() {
        timestamp = LocalDateTime.now();
    }

    RestControllerAdvice(HttpStatus status) {
        this();
        this.status = status;
    }

    @ExceptionHandler(ResourseNotFoundException.class)
    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorInfo smartphoneNotFound(HttpServletRequest req, ResourseNotFoundException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = "control advice at "  + this.timestamp;//messageSource.getMessage("error.no.smartphone.id", null, locale);

       // errorMessage += ex.getSmartphoneId();
        String errorURL = req.getRequestURL().toString();

        return new ErrorInfo(errorURL, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorInfo generalRuntimeExcption(HttpServletRequest req, Exception ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = "Excption control advice at "  + this.timestamp;//messageSource.getMessage("error.no.smartphone.id", null, locale);

        errorMessage += ex.getMessage();
        String errorURL = req.getRequestURL().toString();

        return new ErrorInfo(errorURL, errorMessage);
    }
}
