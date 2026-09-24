package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Cấu trúc response trả về khi có lỗi, dùng chung cho toàn bộ Order Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseError {

    private int status;        // 503, 404, 500...
    private String error;      // "Service Unavailable"
    private String message;    // Mô tả chi tiết
    private String path;       // Endpoint đang gọi
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponseError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
