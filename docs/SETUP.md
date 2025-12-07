# Momon 프로젝트 개발환경 구성 계획

## 목표
Momon MVP 개발을 위한 프론트엔드(Next.js)와 백엔드(Spring Boot) 프로젝트 초기화

## 추천 접근 방식: AI 자동화 구성

### 근거
1. 표준적인 프로젝트 초기화 작업으로 AI가 안전하게 수행 가능
2. PRD에 기술 스택이 명확히 정의되어 있음
3. 수동 작업 대비 시간 절약 및 오류 최소화
4. Jira 티켓(SCRUM-9~16)과 연계하여 체계적 진행 가능

## 실행 단계

### 1단계: 프론트엔드 프로젝트 초기화
**디렉토리:** `/Users/eunkyung/momon/frontend`

**명령어:**
```bash
npx create-next-app@latest frontend --typescript --tailwind --app --no-src-dir --import-alias "@/*"
```

**설정 요구사항:**
- Next.js 14 (최신 안정 버전)
- TypeScript 활성화
- Tailwind CSS 포함
- App Router 사용 (Pages Router 아님)
- src 디렉토리 미사용 (프로젝트 루트에 app 폴더)
- Import alias: `@/*`
- ESLint 활성화

**추가 패키지 설치:**
```bash
cd frontend
npm install uuid html2canvas axios
npm install --save-dev @types/uuid
```

**초기 구조 검증:**
- `app/` 폴더 존재 확인
- `tailwind.config.ts` 존재 확인
- `next.config.js` 존재 확인

---

### 2단계: 백엔드 프로젝트 초기화
**디렉토리:** `/Users/eunkyung/momon/backend`

**방법:** Spring Initializr 사용

**프로젝트 설정:**
- **Project:** Gradle - Groovy
- **Language:** Java
- **Spring Boot:** 3.3.x (최신 안정 버전)
- **Java:** 21 (PRD 명시)
- **Packaging:** Jar
- **Group:** com.momon
- **Artifact:** momon-backend
- **Name:** momon-backend
- **Package name:** com.momon.backend

**의존성 (Dependencies):**
1. **Spring Web** - REST API 개발
2. **Spring Data JPA** - DB ORM
3. **Lombok** - 보일러플레이트 코드 제거
4. **H2 Database** (개발용) - 로컬 테스트
5. **PostgreSQL Driver** (프로덕션용) - 실제 DB
6. **AWS SDK for Java** - S3 연동 (수동 추가 필요)
7. **Spring AI** - GPT-4o, DALL-E 3 연동 (수동 추가 필요)

**명령어 (Spring Initializr CLI):**
```bash
curl https://start.spring.io/starter.zip \
  -d type=gradle-project \
  -d language=java \
  -d bootVersion=3.3.0 \
  -d baseDir=backend \
  -d groupId=com.momon \
  -d artifactId=momon-backend \
  -d name=momon-backend \
  -d packageName=com.momon.backend \
  -d javaVersion=21 \
  -d dependencies=web,data-jpa,lombok,h2,postgresql \
  -o backend.zip && \
  unzip backend.zip && \
  rm backend.zip
```

**수동 추가 작업:**

1. **`build.gradle` 수정:**
   - Spring AI 의존성 추가
   - AWS SDK 의존성 추가
   ```gradle
   dependencies {
       // 기존 의존성...

       // Spring AI
       implementation 'org.springframework.ai:spring-ai-openai-spring-boot-starter:1.0.0-M1'

       // AWS SDK for S3
       implementation 'software.amazon.awssdk:s3:2.20.0'
   }
   ```

2. **`application.yml` 생성:**
   ```yaml
   spring:
     application:
       name: momon-backend

     # Virtual Threads 활성화 (Java 21)
     threads:
       virtual:
         enabled: true

     # Database
     datasource:
       url: jdbc:h2:mem:testdb
       driver-class-name: org.h2.Driver
       username: sa
       password:

     jpa:
       hibernate:
         ddl-auto: update
       show-sql: true

     # CORS (Vercel 도메인 허용)
     web:
       cors:
         allowed-origins:
           - "http://localhost:3000"
           - "https://*.vercel.app"
         allowed-methods: "*"
         allowed-headers: "*"

   # OpenAI API
   openai:
     api-key: ${OPENAI_API_KEY}

   # AWS S3
   aws:
     s3:
       bucket-name: ${AWS_S3_BUCKET_NAME}
       region: ap-northeast-2
   ```

3. **CORS 설정 클래스 작성:**
   - `src/main/java/com/momon/backend/config/WebConfig.java`

---

### 3단계: 디렉토리 구조 검증

**최종 구조:**
```
/Users/eunkyung/momon/
├── prd.md
├── frontend/
│   ├── app/
│   ├── public/
│   ├── tailwind.config.ts
│   ├── next.config.js
│   ├── package.json
│   └── tsconfig.json
└── backend/
    ├── src/
    │   └── main/
    │       ├── java/
    │       │   └── com/momon/backend/
    │       └── resources/
    │           └── application.yml
    ├── build.gradle
    └── gradlew
```

---

### 4단계: 초기 실행 테스트

**프론트엔드:**
```bash
cd frontend
npm run dev
# http://localhost:3000 접속 확인
```

**백엔드:**
```bash
cd backend
./gradlew bootRun
# http://localhost:8080 접속 확인
```

---

## 주의사항

1. **Java 21 설치 확인**
   - `java -version` 명령어로 확인
   - 없으면 SDKMAN 또는 Homebrew로 설치 필요

2. **환경변수 설정 (백엔드)**
   - `OPENAI_API_KEY`: OpenAI API 키
   - `AWS_S3_BUCKET_NAME`: S3 버킷 이름
   - `.env` 파일 또는 IDE 환경변수 설정

3. **Git 설정**
   - 각 프로젝트에 `.gitignore` 자동 생성됨
   - `node_modules/`, `build/`, `.env` 제외 확인

4. **비용 절감 (개발 단계)**
   - DALL-E 호출 부분 주석 처리
   - 고정 이미지 URL 리턴하도록 임시 구현
   - 테스트 완료 후 실제 API 연동

---

## 예상 소요 시간

- **프론트엔드 초기화:** 5분
- **백엔드 초기화:** 10분 (수동 설정 포함)
- **초기 실행 테스트:** 5분
- **총 예상 시간:** 약 20분

---

## 다음 단계 (초기화 완료 후)

1. Jira 티켓 SCRUM-9~16 작업 시작
2. 프론트엔드: Device ID 관리 구현 (SCRUM-9)
3. 백엔드: 유저 식별 로직 구현 (SCRUM-10)
4. API 통신 테스트 (CORS 설정 검증)
