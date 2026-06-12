# 1. 자바 17 또는 21 환경의 경량화 이미지를 기반으로 설정 (본인 자바 버전에 맞추세요)
FROM eclipse-temurin:26-jdk-alpine

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 빌드된 jar 파일을 도커 컨테이너 내부로 복사
COPY build/libs/*-SNAPSHOT.jar app.jar

# 4. 앱 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]