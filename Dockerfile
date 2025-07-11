# 1. 빌드용 JDK 이미지 (멀티 아키텍처 지원)
FROM eclipse-temurin:21-jdk AS build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle wrapper 실행에 필요한 권한 설정
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew

# 4. 전체 소스 복사
COPY . .

# 5. Gradle 빌드 (테스트 제외)
RUN ./gradlew clean build -x test

# 6. 실행용 JRE 이미지
FROM eclipse-temurin:21-jre

# 7. 작업 디렉토리 설정
WORKDIR /app

# 8. 빌드된 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 9. 실행 커맨드 설정
ENTRYPOINT ["java", "-jar", "app.jar"]

# 10. 기본 포트 노출
EXPOSE 8080
