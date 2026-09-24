package com.example.orderservice.controller;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Value("${server.port}")
    private String serverPort;

    public OrderController(OrderRepository orderRepository, ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
    }

    @GetMapping("/info")
    public String info() {
        return "Order Service đang chạy tại port: " + serverPort;
    }

    @GetMapping
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Order> getById(@PathVariable Long id) {
        return orderRepository.findById(id);
    }

    @PostMapping
    public Order create(@RequestBody Order order) {
        // Trước khi tạo order, xác nhận sản phẩm có tồn tại bằng cách gọi
        // thẳng sang Product Service — địa chỉ được tra cứu động qua Eureka.
        // Nếu Product Service không có instance nào -> ném ServiceUnavailableException
        // -> GlobalExceptionHandler bắt và trả về 503 kèm ApiResponseError.
        productServiceClient.getProductById(order.getProductId());
        return orderRepository.save(order);
    }

    /**
     * Endpoint demo: gọi trực tiếp sang PRODUCT-SERVICE thông qua DiscoveryClient,
     * KHÔNG hardcode host/port. Dùng để test Bài tập 4:
     *  - Product Service đổi port (8082 -> 9090) mà không cần sửa code Order Service.
     *  - Product Service tắt -> trả về 503 + ApiResponseError.
     */
    @GetMapping("/products/{productId}")
    public Map<String, Object> getProductViaDiscovery(@PathVariable Long productId) {
        return productServiceClient.getProductById(productId);
    }

    @GetMapping("/products")
    public List<Map<String, Object>> getAllProductsViaDiscovery() {
        return productServiceClient.getAllProducts();
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        return orderRepository.save(order);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderRepository.deleteById(id);
    }
}
