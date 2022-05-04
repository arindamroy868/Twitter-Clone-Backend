package com.twitter.clone.utility;

import ch.qos.logback.core.status.ErrorStatus;
import com.twitter.clone.exception.TwitterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestResponseExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<Map<String,String>> handleValidationException(MethodArgumentNotValidException exception){
        Map<String,String> validationErrorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> validationErrorMap.put(fieldError.getField(),fieldError.getDefaultMessage()));
        return new ResponseEntity<>(validationErrorMap,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {TwitterException.class})
    protected ResponseEntity<ErrorFormat> handleCustomException(Exception e){
        ErrorFormat error = new ErrorFormat(e.getMessage(),HttpStatus.BAD_REQUEST.value(),LocalDateTime.now());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(value = Exception.class)
//    protected  ResponseEntity<ErrorFormat> genericExceptionHandler(Exception e){
//        ErrorFormat error = new ErrorFormat("Something bad happened on server",
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),LocalDateTime.now());
//        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
//    }

}
