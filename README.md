# Đăng ký 3 Service lên Eureka Server

Bộ này gồm 3 microservice độc lập, mỗi service là 1 project Gradle riêng, đều đã cấu hình `spring-cloud-starter-netflix-eureka-client`:

| Service            | Port | Database    | Package                        |
|---------------------|------|-------------|---------------------------------|
| customer-service     | 8081 | customer_db | com.example.customerservice     |
| product-service       | 8082 | product_db  | com.example.productservice      |
| order-service          | 8083 | order_db    | com.example.orderservice        |

## Điều kiện tiên quyết

1. **Eureka Server đã chạy ở port 8761** (project riêng từ session trước, có `@EnableEurekaServer`).
   Nếu chưa có, tạo nhanh 1 project Spring Boot khác với dependency `spring-cloud-starter-netflix-eureka-server`, thêm `@EnableEurekaServer` vào class Application, và cấu hình:
   ```properties
   server.port=8761
   eureka.client.register-with-eureka=false
   eureka.client.fetch-registry=false
   ```
2. MySQL đang chạy ở `localhost:3306`, user `root` / pass `root` (sửa lại trong `application.properties` từng service nếu khác). Không cần tạo database trước — đã bật `createDatabaseIfNotExist=true`.

## Cách chạy

Mở 4 terminal riêng (Eureka Server + 3 service), chạy Eureka Server trước, rồi lần lượt:

```bash
# Terminal 1 - Eureka Server (project riêng)
./gradlew bootRun

# Terminal 2 - Customer Service
cd customer-service
./gradlew bootRun

# Terminal 3 - Product Service
cd product-service
./gradlew bootRun

# Terminal 4 - Order Service
cd order-service
./gradlew bootRun
```

> Lưu ý: các project này chưa có Gradle Wrapper (`gradlew`) sẵn vì được tạo ngoài môi trường có mạng Gradle. Trước khi chạy, mở project bằng IntelliJ IDEA (import Gradle project) — IDE sẽ tự tải wrapper, hoặc chạy `gradle wrapper` nếu máy bạn đã cài Gradle.

## Kiểm tra kết quả

Mở trình duyệt: `http://localhost:8761`

Ở mục **Instances currently registered with Eureka**, bạn sẽ thấy 3 dòng, mỗi dòng ở trạng thái **UP**:

```
CUSTOMER-SERVICE     n/a (1)  (1)  UP (1) - <ip>:customer-service:8081
PRODUCT-SERVICE      n/a (1)  (1)  UP (1) - <ip>:product-service:8082
ORDER-SERVICE        n/a (1)  (1)  UP (1) - <ip>:order-service:8083
```

Test nhanh từng service còn sống và tự đăng ký đúng port:
```bash
curl http://localhost:8081/api/customers/info
curl http://localhost:8082/api/products/info
curl http://localhost:8083/api/orders/info
```

## Cấu trúc chung mỗi service

```
<service>/
 ├── build.gradle                 # dependency: web, jpa, mysql, eureka-client, actuator, lombok
 ├── settings.gradle
 └── src/main/
      ├── java/com/example/<service>/
      │    ├── <Service>Application.java   # @SpringBootApplication + @EnableDiscoveryClient
      │    ├── entity/                     # JPA entity
      │    ├── repository/                 # Spring Data JPA repository
      │    └── controller/                 # REST controller CRUD cơ bản
      └── resources/
           └── application.properties      # port, DB, cấu hình eureka.client...
```

## Những điểm cấu hình Eureka quan trọng cần nhớ

- `spring.application.name` chính là **tên** hiển thị trên Dashboard Eureka (viết hoa tự động). Đây cũng là service-id dùng khi gọi liên service qua `RestTemplate`/`WebClient` có `@LoadBalanced` hoặc qua Feign sau này.
- `eureka.client.service-url.defaultZone` phải trỏ đúng địa chỉ Eureka Server đang chạy.
- `eureka.instance.prefer-ip-address=true` giúp tránh lỗi resolve hostname khi chạy local/Docker.
- `@EnableDiscoveryClient` (dùng chung cho mọi Discovery Server, không riêng Eureka) hoặc có thể thay bằng `@EnableEurekaClient` (chỉ dành riêng Eureka) — cả hai đều hoạt động vì Spring Boot tự nhận diện `eureka-client` có trên classpath và tự đăng ký, kể cả khi không có annotation. Annotation ở đây chủ yếu mang tính tường minh, dễ đọc code.
