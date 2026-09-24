package com.example.orderservice.exception;

import com.example.orderservice.dto.ApiResponseError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Không tìm thấy instance nào trên Eureka -> 503
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiResponseError> handleServiceUnavailable(
            ServiceUnavailableException ex, HttpServletRequest request) {

        ApiResponseError error = new ApiResponseError(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    // Instance có trên Eureka nhưng gọi thực tế thất bại (service vừa bị tắt,
    // timeout, connection refused...) -> cũng trả về 503 cho thân thiện với client
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponseError> handleRestClientException(
            RestClientException ex, HttpServletRequest request) {

        ApiResponseError error = new ApiResponseError(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                "Không thể kết nối tới Product Service: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
