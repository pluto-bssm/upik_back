# 1. Java 21 기반 이미지 사용
FROM eclipse-temurin:21-jdk AS build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle 프로젝트 소스 복사
COPY . .

# 4. Gradle로 프로젝트 빌드
RUN ./gradlew clean build -x test

# 5. 실제 실행에 사용할 경량 JRE 이미지
FROM eclipse-temurin:21-jre

# 6. 작업 디렉토리 설정
WORKDIR /app

# 7. 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 8. 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# 9. 컨테이너가 사용하는 기본 포트
EXPOSE 8080
