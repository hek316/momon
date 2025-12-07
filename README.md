# 🌐 Momon (모몬) - 너의 순간을 몬스터로

> "Make your moment a Monster"

AI가 당신의 사진과 감정을 분석해 세상에 하나뿐인 몬스터 캐릭터로 소환해주는 모바일 웹 서비스

## 📋 프로젝트 개요

- **타겟:** 인스타그램 스토리에 올릴 '힙한 짤'이 필요한 Z세대
- **컨셉:** 사진 + 감정 텍스트 → AI 분석 → 몬스터 캐릭터 생성
- **플랫폼:** Mobile Web (설치 불필요, URL로 즉시 접속)
- **전략:** Simple Sync Architecture (복잡도 0에 수렴하는 구조)

## 🛠 기술 스택

### Frontend
- **Framework:** Next.js 16 (App Router)
- **Language:** TypeScript
- **Styling:** Tailwind CSS
- **주요 라이브러리:**
  - `uuid` - Device ID 생성
  - `html2canvas` - 이미지 캡처
  - `axios` - API 통신
- **Deployment:** Vercel

### Backend
- **Framework:** Spring Boot 3.4.0
- **Language:** Java 21 (Virtual Threads 활성화)
- **Build Tool:** Gradle
- **Database:** H2 (개발) / PostgreSQL (프로덕션)
- **AI:** Spring AI (GPT-4o, DALL-E 3)
- **Storage:** AWS S3
- **Deployment:** AWS EC2

## 📁 프로젝트 구조

```
momon/
├── README.md                 # 이 파일
├── prd.md                    # 프로젝트 요구사항 문서
├── frontend/                 # Next.js 프론트엔드
│   ├── app/                  # App Router 페이지
│   ├── public/               # 정적 파일
│   ├── package.json
│   └── tailwind.config.ts
└── backend/                  # Spring Boot 백엔드
    ├── src/main/
    │   ├── java/com/momon/backend/
    │   │   └── config/
    │   │       └── WebConfig.java     # CORS 설정
    │   └── resources/
    │       └── application.yml        # 애플리케이션 설정
    ├── build.gradle
    └── gradlew
```

## 🚀 시작하기

### 필수 요구사항

- **Node.js:** 18.x 이상
- **Java:** 21 (Virtual Threads 지원)
- **Git:** 최신 버전

### 1. 저장소 클론 (해당되는 경우)

```bash
git clone <repository-url>
cd momon
```

### 2. 프론트엔드 설정

```bash
cd frontend
npm install
npm run dev
```

→ http://localhost:3000 에서 확인

### 3. 백엔드 설정

#### 환경변수 설정

`backend/` 디렉토리에 `.env` 파일 생성 (또는 IDE 환경변수 설정):

```bash
OPENAI_API_KEY=your-openai-api-key-here
AWS_S3_BUCKET_NAME=momon-images
AWS_ACCESS_KEY_ID=your-aws-access-key
AWS_SECRET_ACCESS_KEY=your-aws-secret-key
```

#### 서버 실행

```bash
cd backend
./gradlew bootRun
```

→ http://localhost:8080 에서 확인

### 4. 데이터베이스 콘솔 (개발용 H2)

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (비워두기)

## 🔧 개발환경 초기 설정 (처음 프로젝트를 시작하는 경우)

### 프론트엔드 프로젝트 생성

```bash
npx create-next-app@latest frontend --typescript --tailwind --app --no-src-dir --import-alias "@/*"
cd frontend
npm install uuid html2canvas axios
npm install --save-dev @types/uuid
```

### 백엔드 프로젝트 생성

```bash
curl https://start.spring.io/starter.zip \
  -d type=gradle-project \
  -d language=java \
  -d bootVersion=3.4.0 \
  -d baseDir=backend \
  -d groupId=com.momon \
  -d artifactId=momon-backend \
  -d name=momon-backend \
  -d packageName=com.momon.backend \
  -d javaVersion=21 \
  -d dependencies=web,data-jpa,lombok,h2,postgresql \
  -o backend.zip
unzip backend.zip && rm backend.zip
```

**수동 작업 필요:**
1. `build.gradle`에 Spring AI, AWS SDK 의존성 추가
2. `application.yml` 생성 및 설정
3. `WebConfig.java` CORS 설정 작성

→ 상세 내용은 `/docs/setup.md` 참조 (또는 Claude에게 물어보세요!)

## 📋 API 명세

### 1. 몬스터 생성
```http
POST /api/v1/monsters
Header: X-Device-ID: {uuid}
Content-Type: multipart/form-data

Body:
  - image: File
  - text: String (max 100자)

Response:
{
  "id": 1,
  "imageUrl": "https://s3.../monster.png",
  "name": "월요병 슬라임",
  "description": "피곤에 쩔어있는..."
}
```

### 2. 내 도감 조회
```http
GET /api/v1/monsters
Header: X-Device-ID: {uuid}

Response:
[
  {
    "id": 1,
    "imageUrl": "https://s3.../monster.png",
    "name": "월요병 슬라임",
    "description": "피곤에 쩔어있는...",
    "createdAt": "2025-12-07T12:00:00"
  },
  ...
]
```

## 🎯 주요 기능 (MVP)

1. **유저 식별 (No Login)**
   - LocalStorage 기반 Device ID 관리
   - 로그인 없이 브라우저별 유저 구분

2. **몬스터 생성**
   - 이미지 + 감정 텍스트 업로드
   - GPT-4o 분석 → DALL-E 3 이미지 생성
   - S3 영구 저장

3. **도감 & 공유**
   - 생성된 몬스터 리스트 조회
   - html2canvas 기반 이미지 저장
   - 인스타그램 공유 최적화

## 🎨 Jira 티켓 정보

프로젝트는 Jira로 관리되고 있습니다:

- **Epic:** SCRUM-5 - Momon MVP 개발
- **Stories:**
  - SCRUM-6: 유저 식별 (FE: SCRUM-9, BE: SCRUM-10)
  - SCRUM-7: 몬스터 생성 (FE: SCRUM-11, BE: SCRUM-12, SCRUM-13)
  - SCRUM-8: 도감 및 저장 (FE: SCRUM-14, SCRUM-15, BE: SCRUM-16)

## 💡 개발 팁

### 비용 절감 (개발 단계)
DALL-E API는 사용량에 따라 과금됩니다. 개발 중에는:
```java
// MonsterService.java에서 DALL-E 호출 부분 주석 처리
// 고정 이미지 URL 리턴
return "https://s3.../sample-monster.png";
```

### CORS 에러 해결
- 프론트엔드 도메인이 변경되면 `WebConfig.java`의 `allowedOrigins` 업데이트
- Vercel 배포 시 자동으로 `https://*.vercel.app` 허용됨

### Virtual Threads 활용
Java 21의 Virtual Threads가 `application.yml`에서 활성화되어 있습니다.
동시 요청 처리 성능이 향상되어 AI API 호출에 유리합니다.

## 📚 참고 문서

- [PRD (Product Requirements Document)](./prd.md)
- [Jira Board](https://momon2.atlassian.net)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Next.js Documentation](https://nextjs.org/docs)

## 🤝 기여하기

1. Feature 브랜치 생성 (`git checkout -b feature/amazing-feature`)
2. 변경사항 커밋 (`git commit -m 'Add amazing feature'`)
3. 브랜치 푸시 (`git push origin feature/amazing-feature`)
4. Pull Request 생성

## 📞 문의

프로젝트 관련 문의사항은 Jira 또는 팀 Slack 채널을 이용해주세요.

---

**Made with ❤️ for Z-Generation**
