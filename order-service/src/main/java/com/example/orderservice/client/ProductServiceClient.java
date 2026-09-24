package com.example.orderservice.client;

import com.example.orderservice.exception.ServiceUnavailableException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class ProductServiceClient {

    // Phải trùng với spring.application.name bên Product Service (Eureka tự viết hoa hết)
    private static final String SERVICE_ID = "PRODUCT-SERVICE";

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    public ProductServiceClient(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    // URL dùng TÊN LOGIC của service (trùng spring.application.name, viết hoa hay
    // thường đều được vì LoadBalancer tra cứu không phân biệt hoa/thường),
    // KHÔNG phải host:port thật. @LoadBalanced RestTemplate sẽ tự thay thế
    // phần "PRODUCT-SERVICE" bằng 1 host:port cụ thể trước khi gửi request.
    private static final String BASE_URL = "http://" + SERVICE_ID;

    /**
     * Kiểm tra nhanh trước xem Eureka có đang giữ instance nào của PRODUCT-SERVICE
     * không, để chủ động ném lỗi 503 với thông điệp rõ ràng — thay vì để lộ ra
     * exception nội bộ (khó đọc) mà LoadBalancer ném khi không có instance nào.
     */
    private void ensureServiceAvailable() {
        List<ServiceInstance> instances = discoveryClient.getInstances(SERVICE_ID);

        if (instances == null || instances.isEmpty()) {
            throw new ServiceUnavailableException(
                    "Không tìm thấy instance nào của " + SERVICE_ID + " đang hoạt động trên Eureka. " +
                    "Có thể service chưa khởi động, đang khởi động dở, hoặc đã bị tắt."
            );
        }
    }

    /**
     * Lấy chi tiết 1 sản phẩm từ Product Service.
     * Request được LoadBalancer tự động phân phối luân phiên (round-robin)
     * giữa các instance đang UP — không quan tâm port cụ thể là bao nhiêu.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProductById(Long productId) {
        ensureServiceAvailable();
        String url = BASE_URL + "/api/products/" + productId;
        return restTemplate.getForObject(url, Map.class);
    }

    /**
     * Lấy toàn bộ danh sách sản phẩm từ Product Service.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllProducts() {
        ensureServiceAvailable();
        String url = BASE_URL + "/api/products";
        return restTemplate.getForObject(url, List.class);
    }
}
