---

# 🌐 [Final PRD] Project: Momon (모몬) - Web Edition
> **Type:** Mobile Web Service (Not App)
> **Stack:** Spring Boot (Brain) + Next.js (Face)
> **Strategy:** **"Simple Sync Architecture"** (복잡도 0에 수렴하는 구조)
> **Timeline:** 3 Weeks (MVP)

---

## 1. 서비스 개요 (Overview)
*   **Service Name:** **Momon (모몬)**
*   **Slogan:** "너의 순간을 몬스터로, **모몬** (Make your moment a Monster)."
*   **Concept:** 나의 사진과 감정 일기를 AI가 분석해, 세상에 하나뿐인 '몬스터 캐릭터'로 소환해 주는 웹 서비스.
*   **Target:** 인스타그램 스토리에 올릴 '힙한 짤'이 필요한 Z세대.
*   **Access:** 인스타 프로필 링크 or URL 클릭 → **설치 없이 즉시 실행**.

---

## 2. 사용자 시나리오 (Web User Flow)
1.  **[접속]** URL(`momon.xyz`) 접속 (로그인 없음).
2.  **[작성]** `[+]` 버튼 → 갤러리 사진 선택 + 한 줄 감정 입력(100자).
3.  **[소환]** "몬스터 소환 중..." (로딩 화면 유지, 약 30~50초).
4.  **[결과]** 몬스터 카드(이미지+이름) 탄생.
5.  **[소장]**
    *   **저장:** 이미지 꾹 눌러서 저장 (Web Native).
    *   **공유:** '캡처해서 공유하기' 버튼 클릭.

---

## 3. 핵심 기능 명세 (Functional Requirements)

### ① 유저 식별 (No Login, Browser based)
*   **Front (Next.js):**
    *   사용자가 처음 접속하면 `uuid`를 생성하여 브라우저 **LocalStorage**에 저장.
    *   모든 API 요청 시 Header에 `X-Device-ID`로 실어 보냄.
*   **Back (Spring):**
    *   `X-Device-ID`가 DB에 없으면 신규 유저로 간주(`users` 테이블 INSERT).

### ② 몬스터 생성 (The Core - Sync)
*   **Flow:** 요청 → 대기(90초 Timeout) → 결과 (단방향 동기 처리).
*   **Logic (Spring Boot):**
    1.  `MultipartFile`과 텍스트 수신.
    2.  **Spring AI (GPT-4o)**: 이미지+텍스트 분석 → 몬스터 특징(JSON) 추출.
    3.  **DALL-E 3**: 이미지 생성 요청.
    4.  **S3 Upload (필수)**: 생성된 이미지 URL을 받아서 내 S3 버킷에 업로드. (OpenAI URL 사용 금지).
    5.  **DB Save**: S3 URL, 이름, 프롬프트 저장.
    6.  **Response**: 최종 JSON 반환.
*   **Web UX:**
    *   생성 중 브라우저 탭을 닫지 말라는 경고 문구 노출 (`window.onbeforeunload`).
    *   30초 이상 걸릴 때 "거의 다 왔어요!" 같은 멘트 롤링.

### ③ 도감 및 저장 (Gallery & Share)
*   **도감:** 메인 화면 접속 시, 내 Device ID로 생성된 몬스터 리스트 조회.
*   **저장/공유 (Web Specific):**
    *   웹은 앱처럼 네이티브 공유가 제한적임.
    *   **전략:** `html2canvas` 라이브러리를 사용해 결과 카드 영역을 이미지로 변환 → 다운로드 트리거.

---

## 4. 기술 아키텍처 (Tech Stack)

### 🏛 Backend (Spring Boot 3.3)
*   **Language:** **Java 21** (Virtual Threads `enabled: true` 필수).
*   **Infrastructure:** AWS EC2 (t2.micro) + **Jar 직접 실행**.
*   **Storage:** AWS S3 (이미지 영구 저장).
*   **CORS Config:** 프론트엔드 도메인(Vercel) 허용 설정 필수.

### 💻 Frontend (Next.js 14)
*   **Framework:** Next.js (App Router).
*   **Styling:** Tailwind CSS (AI에게 "검정 배경에 형광 초록 포인트 UI 짜줘" 시키기 최적화).
*   **Deployment:** **Vercel** (GitHub 연결 시 자동 배포, 무료).

---

## 5. API 명세 (Simple)

### 1. 몬스터 소환
*   **POST** `/api/v1/monsters`
*   **Header:** `X-Device-ID: {uuid}`
*   **Body:** `form-data` (image, text)
*   **Response (200 OK):**
    ```json
    {
      "id": 1,
      "imageUrl": "https://s3.ap-northeast-2.../monster.png",
      "name": "월요병 슬라임",
      "description": "피곤에 쩔어있는..."
    }
    ```

### 2. 내 도감 조회
*   **GET** `/api/v1/monsters`
*   **Header:** `X-Device-ID: {uuid}`
*   **Response:** 몬스터 객체 리스트 (`List<MonsterResponse>`).

---

## 6. 개발 로드맵 (3 Weeks Sprint)

### 🟢 1주 차: 백엔드 & 인프라 (Backend First)
*   **Goal:** "Postman으로 요청 보내면 S3에 저장되고 DB에 남는다."
*   **Tasks:**
    *   Spring Boot 프로젝트 세팅 (Java 21, Spring AI).
    *   AWS EC2 생성, S3 버킷 생성.
    *   **CORS 설정** (`WebMvcConfigurer`).
    *   API 구현 (`POST /generate`).

### 🟡 2주 차: 프론트엔드 & AI (Make it Visual)
*   **Goal:** "웹사이트에서 사진 올리면 몬스터가 뜬다."
*   **Tasks:**
    *   ChatGPT에게 Next.js + Tailwind 코드 생성 요청 (메인, 입력, 결과 페이지).
    *   Vercel에 배포하여 EC2 백엔드와 통신 테스트.
    *   **프롬프트 엔지니어링:** 몬스터가 귀엽게 나오도록 GPT 프롬프트 깎기 (제일 중요).

### 🔴 3주 차: 다듬기 & 런칭 (Polish)
*   **Goal:** "친구에게 링크를 보내면 자랑한다."
*   **Tasks:**
    *   로딩 화면 귀엽게 꾸미기 (Lottie 활용).
    *   결과 화면 이미지 저장 기능 구현 (`html2canvas`).
    *   도메인 연결 (`momon.xyz` 등) 및 HTTPS 확인.
    *   지인 배포 및 런칭!

---

## 7. 4년 차 개발자를 위한 마지막 팁

1.  **CORS 에러 100% 납니다:**
    *   미리 `WebConfig.java`에 `allowedOrigins("https://*.vercel.app")` 추가해 두세요.
2.  **비용 아끼기:**
    *   개발 테스트할 때는 DALL-E 호출 부분 주석 처리하고, 고정된 이미지 URL(S3에 있는 샘플)을 리턴하도록 하세요. (돈 나갑니다).
3.  **프론트는 AI에게 맡기세요:**
    *   "Next.js로 모바일 뷰 카드 UI 만들어줘. 배경은 #111111이고 텍스트는 #00FF00이야."라고 구체적으로 시키면 님보다 잘 짭니다. 님은 백엔드 안정성에 집중하세요.
