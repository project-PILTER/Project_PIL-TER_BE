# Project_LimC-BE

my-java-project/
├── .gradle/                # Gradle 빌드 캐시 및 설정 파일 (자동 생성)
├── .idea/                  # IntelliJ IDEA 프로젝트 설정 폴더 (자동 생성)
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/           # 실제 작성할 Java 소스 코드 (.java)
│   │   │   └── com/ll/projectLimC/
│   │   │       ├── Application.java      # 메인 실행 클래스
│   │   │       ├── controller/           # 사용자의 요청을 처리하는 계층
│   │   │       ├── service/              # 비즈니스 로직 처리 계층
│   │   │       ├── repository/           # 데이터베이스 접근 계층 (DAO)
│   │   │       └── dto/                  # 데이터 전송 객체 (Data Transfer Object)
│   │   └── resources/      # 설정 파일, 정적 파일, 템플릿 등 (application.properties 등)
│   └── test/
│       ├── java/           # 테스트 코드 디렉터리 (JUnit 등)
│       └── resources/      # 테스트용 설정 파일
├── .gitignore              # Git 버전 관리에서 제외할 파일 목록 설정
├── build.gradle            # Gradle 빌드 스크립트 및 의존성(라이브러리) 관리
├── gradlew                 # Mac/Linux용 Gradle 빌드 실행 스크립트
├── gradlew.bat             # Windows용 Gradle 빌드 실행 스크립트
└── settings.gradle         # 프로젝트 이름 및 모듈 설정
