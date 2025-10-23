# Giai đoạn 1: Build ứng dụng với Maven
FROM maven:3.8.5-openjdk-17 AS build

# Sao chép mã nguồn vào container
WORKDIR /app
COPY . .

# Build ứng dụng và bỏ qua các kiểm thử (để build nhanh hơn trong CI/CD)
RUN mvn clean package -DskipTests

# Giai đoạn 2: Tạo một image nhỏ hơn để chạy ứng dụng
FROM openjdk:17-jdk-slim

# Tạo một người dùng và nhóm không phải là root
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Sao chép tệp JAR đã được build từ giai đoạn trước
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Thiết lập các biến môi trường (ví dụ)
ENV SPRING_PROFILES_ACTIVE=prod

# Cổng mà ứng dụng sẽ lắng nghe
EXPOSE 8080

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java","-jar","/app/app.jar"]
