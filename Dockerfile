# Step 1: OpenJDK 17 기반 이미지로 시작
FROM eclipse-temurin:21-jdk-jammy as build

# Step 2: Spring Boot 애플리케이션을 위한 작업 디렉토리 설정
WORKDIR /app

# Step 3: 빌드된 Spring Boot JAR 파일을 컨테이너로 복사
COPY build/libs/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Step 4: Spring Boot 애플리케이션 포트 8080 노출
EXPOSE 8080

# Step 5: Spring Boot 애플리케이션 실행 명령어
CMD ["java", "-jar", "app.jar"]
