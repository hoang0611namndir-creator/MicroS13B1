package com.example.orderservice.exception;

/**
 * Ném ra khi DiscoveryClient.getInstances(serviceName) trả về danh sách rỗng,
 * tức là service đó chưa đăng ký, hoặc đã bị tắt / rớt khỏi Eureka.
 */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
