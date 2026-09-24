package com.example.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * @LoadBalanced "gắn phép" cho RestTemplate này: mọi request gửi tới 1 URL
     * dùng TÊN LOGIC của service (vd: http://PRODUCT-SERVICE/...) sẽ bị
     * LoadBalancerInterceptor chặn lại, hỏi Spring Cloud LoadBalancer chọn ra
     * 1 instance cụ thể (mặc định round-robin) trong số các instance mà
     * DiscoveryClient lấy được từ Eureka, rồi mới thay tên logic bằng
     * host:port thật trước khi thực sự gửi request đi.
     *
     * Vì vậy code gọi API (ProductServiceClient) không cần biết port là bao nhiêu,
     * và không cần tự tay chọn instance nào nữa — LoadBalancer lo hết.
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
