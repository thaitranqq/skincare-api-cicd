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
├── .github/                 # Cấu hình GitHub Actions (CI/CD)
├── src/
│   ├── main/
│   │   ├── java/            # Mã nguồn Java của ứng dụng
│   │   └── resources/       # Tệp cấu hình, script SQL, v.v.
│   │       └── db/migration/ # Các script di chuyển cơ sở dữ liệu của Flyway
│   └── test/                # Mã nguồn kiểm thử
├── Dockerfile               # Định nghĩa để build Docker image cho ứng dụng
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
(Xem chi tiết tại `http://localhost:8080/swagger-ui.html` sau khi khởi động ứng dụng)

## Triển khai với Docker

1.  **Build Docker Image**:
    ```bash
    docker build -t your-app-name:latest .
    ```

2.  **Chạy Docker Container**:
    ```bash
    docker run -p 8080:8080 \
      -e SPRING_DATASOURCE_URL="jdbc:mysql://<your_db_host>:3306/geniedb" \
      -e SPRING_DATASOURCE_USERNAME="your_db_user" \
      -e SPRING_DATASOURCE_PASSWORD="your_db_password" \
      -e APP_JWT_SECRET="your_super_secret_jwt_key" \
      your-app-name:latest
    ```

## Tích hợp và Triển khai liên tục (CI/CD)
Dự án này được cấu hình để sử dụng GitHub Actions cho CI/CD (xem thư mục `.github/workflows`). Quy trình làm việc mẫu thường bao gồm các bước sau:
1.  **Trigger**: Kích hoạt khi có push lên nhánh `main` hoặc khi tạo Pull Request.
2.  **Build & Test**: Checkout mã nguồn, thiết lập JDK, và chạy `./mvnw clean install` để biên dịch và thực thi các bài kiểm thử.
3.  **Build Docker Image**: Nếu các bài kiểm thử thành công, build Docker image và đẩy lên một registry (ví dụ: Docker Hub, GitHub Container Registry).
4.  **Deploy**: (Tùy chọn) Tự động triển khai image lên một môi trường (staging hoặc production).

## Hướng dẫn Đóng góp

1.  **Fork & Clone**: Fork repository về tài khoản của bạn và clone về máy.
2.  **Tạo Nhánh**: Tạo một nhánh mới từ nhánh `develop` (hoặc `main` nếu không có `develop`) cho mỗi tính năng hoặc bản sửa lỗi: `git checkout -b feature/ten-tinh-nang`.
3.  **Commit Changes**: Thực hiện các thay đổi và commit với một thông điệp rõ ràng theo quy ước (ví dụ: `feat: Add user authentication`).
4.  **Push và Pull Request**: Đẩy nhánh của bạn lên repository đã fork và tạo một Pull Request (PR) tới nhánh `develop` của repository gốc.

## Lộ trình Phát triển (Roadmap)
*   **Q2 2024**: Hoàn thiện các tính năng cốt lõi, cải thiện độ bao phủ của kiểm thử.
*   **Q3 2024**: Tích hợp thêm các nhà cung cấp OAuth2, triển khai hệ thống thông báo đẩy.
*   **Q4 2024**: Tối ưu hóa hiệu năng, xem xét triển khai kiến trúc microservices cho các thành phần cụ thể.

## Giấy phép
Dự án này được cấp phép theo Giấy phép MIT.
