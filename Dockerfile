# 1. 빌드용 JDK 이미지 (아키텍처 자동 대응)
FROM eclipse-temurin:21-jdk AS build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 소스 복사
COPY . .

# 4. Gradle 빌드 (테스트 제외)
RUN ./gradlew clean build -x test

# 5. 실행용 JRE 이미지 (아키텍처 자동 대응)
FROM eclipse-temurin:21-jre

# 6. 작업 디렉토리 설정
WORKDIR /app

# 7. 빌드된 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 8. 실행 커맨드 설정
ENTRYPOINT ["java", "-jar", "app.jar"]

# 9. 기본 포트 노출
EXPOSE 8080
