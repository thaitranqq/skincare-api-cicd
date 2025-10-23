# Ứng dụng Web Demo với Spring Boot và Azure

## Mô tả
Đây là một ứng dụng web RESTful được xây dựng bằng Spring Boot. Ứng dụng cung cấp các chức năng nghiệp vụ cốt lõi bao gồm quản lý dữ liệu, xác thực và ủy quyền người dùng, gửi email và tích hợp với dịch vụ lưu trữ đám mây Azure Blob Storage. Dự án được thiết kế để có thể mở rộng và bảo trì, với cơ sở dữ liệu được quản lý bởi Flyway và tài liệu API được tạo tự động.

## Tính năng chính
*   **API RESTful**: Cung cấp các điểm cuối HTTP để tương tác với các tài nguyên của ứng dụng.
*   **Quản lý dữ liệu**: Sử dụng Spring Data JPA và Hibernate để tương tác với cơ sở dữ liệu MySQL.
*   **Xác thực và Ủy quyền**: Bảo mật ứng dụng với Spring Security, hỗ trợ xác thực dựa trên OAuth2 Client và mã thông báo JWT.
*   **Lưu trữ tệp**: Tích hợp với Azure Blob Storage để tải lên, tải xuống và quản lý tệp.
*   **Gửi email**: Chức năng gửi email thông qua Spring Mail.
*   **API Documentation**: Tự động tạo và hiển thị tài liệu API tương tác bằng Springdoc OpenAPI.
*   **Di chuyển cơ sở dữ liệu**: Quản lý các phiên bản lược đồ cơ sở dữ liệu một cách nhất quán bằng Flyway.

## Công nghệ sử dụng
*   **Backend**: Java 17, Spring Boot (Web, Data JPA, Security, Mail, OAuth2 Client, Thymeleaf)
*   **Cơ sở dữ liệu**: MySQL, H2 (cho phát triển/kiểm thử)
*   **ORM**: Hibernate (qua Spring Data JPA)
*   **Di chuyển DB**: Flyway
*   **Bảo mật**: Spring Security, JWT, OAuth2 Client
*   **Lưu trữ đám mây**: Azure Storage Blob
*   **API Docs**: Springdoc OpenAPI
*   **Công cụ hỗ trợ**: Lombok, MapStruct
*   **Build Tool**: Maven
*   **Containerization**: Docker, Docker Compose

## Cấu trúc dự án
```
demo/
├── .mvn/                   # Maven wrapper
├── src/
│   ├── main/
│   │   ├── java/            # Mã nguồn Java của ứng dụng
│   │   └── resources/       # Tệp cấu hình, script SQL, v.v.
│   │       └── db/migration/ # Các script di chuyển cơ sở dữ liệu của Flyway
│   └── test/                # Mã nguồn kiểm thử
├── .gitignore               # Các tệp và thư mục bị Git bỏ qua
├── docker-compose.yml       # Định nghĩa các dịch vụ để phát triển
├── mvnw, mvnw.cmd           # Maven wrapper
├── pom.xml                  # Tệp cấu hình dự án của Maven
└── README.md                # Tài liệu hướng dẫn dự án
```

## Bắt đầu

### Yêu cầu
*   Java Development Kit (JDK) 17+
*   Apache Maven 3.6+
*   Docker và Docker Compose

### Cài đặt và Chạy

1.  **Clone repository**: `git clone <URL_CỦA_REPOSITORY> && cd demo`

2.  **Chạy Cơ sở dữ liệu với Docker Compose**:
    ```bash
    docker-compose up -d
    ```
    Lệnh này sẽ khởi động một dịch vụ MySQL với các thông số sau: **Port**: `3306`, **Database**: `geniedb`, **User**: `genie`, **Password**: `1234`.

3.  **Cấu hình ứng dụng**:
    Tạo tệp `application.properties` trong `src/main/resources` và cấu hình các thuộc tính cần thiết. Sử dụng các biến môi trường cho các giá trị nhạy cảm trong môi trường sản xuất.

    ```properties
    # Database (cho Docker Compose)
    spring.datasource.url=jdbc:mysql://localhost:3306/geniedb?useSSL=false&serverTimezone=UTC
    spring.datasource.username=genie
    spring.datasource.password=1234
    spring.jpa.hibernate.ddl-auto=validate
    spring.flyway.enabled=true

    # JWT
    app.jwt.secret=your_jwt_secret_key
    app.jwt.expiration=86400000
    ```

4.  **Build và Chạy ứng dụng**:
    ```bash
    ./mvnw spring-boot:run
    ```
    Ứng dụng sẽ chạy tại `http://localhost:8080`.

## API Endpoints

### Authentication (`/api/v1/auth`)
*   `POST /signup`: Đăng ký người dùng mới.
*   `POST /signin`: Đăng nhập, trả về access và refresh tokens.
*   `POST /refresh`: Làm mới access token.
*   `GET /me`: Lấy thông tin người dùng hiện tại.

### User Profile (`/api/v1/profile`)
*   `GET /`: Lấy hồ sơ người dùng.
*   `PUT /`: Cập nhật hồ sơ người dùng.

### Administration (`/api/v1/admin`)
*   `GET /trends/users`: Lấy dữ liệu xu hướng người dùng.
*   `GET /events`: Lấy danh sách các sự kiện hệ thống.

(Xem chi tiết tại `http://localhost:8080/swagger-ui.html`)

## Xử lý lỗi
Ứng dụng sử dụng một `GlobalExceptionHandler` (`@ControllerAdvice`) để bắt các ngoại lệ và trả về các phản hồi lỗi JSON nhất quán.

*   **Cấu trúc phản hồi lỗi**:
    ```json
    {
      "error": "Mô tả lỗi chi tiết"
    }
    ```
*   **Các mã trạng thái HTTP phổ biến**:
    *   `400 Bad Request`: Đối với các yêu cầu không hợp lệ (ví dụ: thiếu trường bắt buộc).
    *   `401 Unauthorized`: Khi xác thực thất bại hoặc thiếu thông tin xác thực.
    *   `403 Forbidden`: Khi người dùng không có quyền truy cập tài nguyên.
    *   `404 Not Found`: Khi tài nguyên được yêu cầu không tồn tại.
    *   `500 Internal Server Error`: Đối với các lỗi không mong muốn phía máy chủ.

## Các vấn đề về bảo mật
*   **Xác thực JWT**: Luồng xác thực hoạt động như sau:
    1.  Người dùng gửi thông tin đăng nhập (`/api/v1/auth/signin`).
    2.  Máy chủ xác thực và trả về một `accessToken` (ngắn hạn) và một `refreshToken` (dài hạn).
    3.  Client gửi `accessToken` trong header `Authorization` (dưới dạng `Bearer <token>`) cho mỗi yêu cầu cần xác thực.
    4.  Nếu `accessToken` hết hạn, client sử dụng `refreshToken` để nhận một `accessToken` mới.
*   **HTTPS**: Trong môi trường sản xuất, luôn sử dụng HTTPS để mã hóa tất cả lưu lượng truy cập giữa client và máy chủ, bảo vệ dữ liệu nhạy cảm như mã thông báo và thông tin người dùng.
*   **Xác thực đầu vào**: Ứng dụng thực hiện xác thực đầu vào để chống lại các cuộc tấn công như SQL Injection và Cross-Site Scripting (XSS).

## Quản lý Di chuyển Cơ sở dữ liệu với Flyway
Flyway tự động quản lý và áp dụng các thay đổi lược đồ cơ sở dữ liệu khi ứng dụng khởi động.
*   **Vị trí Script**: Các tập lệnh di chuyển SQL được đặt tại `src/main/resources/db/migration`.
*   **Quy ước đặt tên**: Các tập lệnh phải tuân theo quy ước `V<VERSION>__<DESCRIPTION>.sql`. Ví dụ: `V1__Create_user_table.sql`.
*   **Cách hoạt động**: Flyway kiểm tra bảng `flyway_schema_history` để xác định các tập lệnh di chuyển mới cần được áp dụng.

## Ghi nhật ký (Logging)
*   **Cấu hình**: Spring Boot sử dụng Logback làm cơ chế ghi nhật ký mặc định. Bạn có thể tùy chỉnh cấp độ ghi nhật ký trong `application.properties`:
    ```properties
    # Đặt cấp độ ghi nhật ký mặc định là INFO
    logging.level.root=INFO
    # Đặt cấp độ DEBUG cho mã nguồn của ứng dụng
    logging.level.com.example.demo=DEBUG
    ```
*   **Đầu ra**: Nhật ký được in ra console theo mặc định. Trong môi trường sản xuất, bạn nên cấu hình để ghi nhật ký vào tệp và sử dụng các công cụ tổng hợp nhật ký.

## Quản lý Cấu hình Nâng cao
Spring Boot hỗ trợ các hồ sơ (profiles) để quản lý cấu hình cho các môi trường khác nhau.
*   **Tệp cấu hình theo hồ sơ**: Bạn có thể tạo các tệp `application-{profile}.properties` (ví dụ: `application-dev.properties`, `application-prod.properties`).
*   **Kích hoạt hồ sơ**: Đặt thuộc tính `spring.profiles.active=your_profile` trong `application.properties` hoặc thông qua biến môi trường `SPRING_PROFILES_ACTIVE`.

## Phong cách mã hóa và Quy ước
*   **Định dạng mã**: Sử dụng các quy ước định dạng mã Java tiêu chuẩn. Hầu hết các IDE đều có thể tự động định dạng mã.
*   **Lombok**: Dự án sử dụng Lombok để giảm mã soạn sẵn. Hãy tận dụng các chú thích như `@Data`, `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`.
*   **Quy ước đặt tên**: Tuân thủ các quy ước đặt tên tiêu chuẩn của Java (ví dụ: `camelCase` cho các phương thức và biến, `PascalCase` cho các lớp).

## Khắc phục sự cố
*   **Lỗi `Port 8080 already in use`**: Một tiến trình khác đang sử dụng cổng 8080. Dừng tiến trình đó hoặc thay đổi cổng của ứng dụng bằng cách thêm `server.port=new_port` vào `application.properties`.
*   **Lỗi kết nối cơ sở dữ liệu**: Đảm bảo container Docker MySQL đang chạy (`docker ps`). Kiểm tra lại thông tin đăng nhập và URL trong `application.properties`.
*   **Lỗi Flyway `Validate failed`**: Lược đồ cơ sở dữ liệu hiện tại không khớp với các tập lệnh di chuyển. Điều này có thể xảy ra nếu bạn thay đổi một tập lệnh đã được áp dụng. Để khắc phục, bạn có thể cần phải sửa chữa lược đồ Flyway (`./mvnw flyway:repair`) hoặc xóa và tạo lại cơ sở dữ liệu.

## Chạy Kiểm thử
```bash
./mvnw test
```

## Triển khai
```bash
./mvnw clean package
```
Tệp JAR thực thi sẽ được tạo tại `target/demo-0.0.1-SNAPSHOT.jar`.

## Đóng góp và Giấy phép
Dự án này được cấp phép theo Giấy phép MIT. Vui lòng fork repository và gửi pull request để đóng góp.
