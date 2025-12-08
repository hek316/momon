# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Momon (모몬)** is a mobile web service that uses AI to transform user photos and emotions into unique monster characters. The architecture follows a "Simple Sync Architecture" pattern with synchronous request-response flow.

- **No Login Required**: Users identified by browser-based Device ID (LocalStorage + uuid)
- **Sync AI Processing**: Image upload → GPT-4o analysis → DALL-E 3 generation → S3 storage (90s timeout)
- **Target Users**: Z-Generation users creating shareable content for Instagram Stories

## Architecture

### Monorepo Structure
```
momon/
├── frontend/     # Next.js 16 (App Router) + TypeScript + Tailwind CSS
└── backend/      # Spring Boot 3.4.0 + Java 21 + Spring AI
```

### Key Architectural Decisions

**Frontend (Next.js):**
- Uses App Router (not Pages Router) - routes live in `app/` directory
- Device ID management via LocalStorage (no authentication)
- All API requests include `X-Device-ID` header via axios interceptor
- Image capture via `html2canvas` (web platform limitation for sharing)

**Backend (Spring Boot):**
- Java 21 with Virtual Threads enabled for improved AI API concurrency
- H2 in-memory database for development, PostgreSQL for production
- CORS configured for `localhost:3000` and `*.vercel.app`
- Spring AI integration for OpenAI (GPT-4o + DALL-E 3)
- AWS S3 for permanent image storage (OpenAI URLs must NOT be used directly)

### Critical Architectural Constraints

1. **Synchronous Processing Only**: No queues, no webhooks. Client waits for AI processing (30-50s typical)
2. **Device ID as Primary Key**: User entity linked by browser UUID, not email/phone
3. **S3 Upload Mandatory**: DALL-E generated images must be downloaded and re-uploaded to S3
4. **CORS Critical**: Frontend (Vercel) and backend (EC2) on different domains

## Development Commands

### Docker (Recommended for Full-Stack Development)
```bash
# Setup environment variables first
cp .env.example .env
# Edit .env and add your API keys

# Start all services (PostgreSQL + Backend + Frontend)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Rebuild after code changes
docker-compose up -d --build

# Reset database (removes all data)
docker-compose down -v
```

**Services:**
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- PostgreSQL: localhost:5432 (DB: momon, User: momon, Pass: momon123)

### Frontend (Local Development without Docker)
```bash
cd frontend
npm install          # Install dependencies
npm run dev          # Start dev server (http://localhost:3000)
npm run build        # Production build
npm run lint         # Run ESLint
```

### Backend (Local Development without Docker)
```bash
cd backend
./gradlew bootRun    # Start server (http://localhost:8080) - stays at 80% while running
./gradlew build      # Build project
./gradlew test       # Run tests
./gradlew clean      # Clean build artifacts
```

**Note**:
- `./gradlew bootRun` shows "80% EXECUTING" while server is running - this is normal, not a hang.
- Local backend uses H2 in-memory database. Docker uses PostgreSQL (production-like).

### Database Console (Development)
- H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave blank)

## Environment Variables

Backend requires these environment variables (set in IDE or `.env` file):

```bash
OPENAI_API_KEY=sk-...              # OpenAI API key for GPT-4o and DALL-E 3
AWS_S3_BUCKET_NAME=momon-images    # S3 bucket for monster images
AWS_ACCESS_KEY_ID=...              # AWS credentials
AWS_SECRET_ACCESS_KEY=...          # AWS credentials
```

## API Contract

All API endpoints use `X-Device-ID` header for user identification.

### POST /api/v1/monsters
- **Purpose**: Generate monster from image + emotion text
- **Content-Type**: `multipart/form-data`
- **Body**: `image` (File), `text` (String, max 100 chars)
- **Response**: `{ id, imageUrl, name, description }`
- **Processing Flow**:
  1. Receive image + text
  2. GPT-4o analyzes image/text → extract monster characteristics
  3. DALL-E 3 generates image
  4. Download from OpenAI URL → Upload to S3
  5. Save to DB (S3 URL, not OpenAI URL)
  6. Return JSON

### GET /api/v1/monsters
- **Purpose**: Retrieve user's monster collection
- **Response**: Array of monster objects, sorted by creation date (newest first)

## Code Organization Patterns

### Backend Package Structure
```
com.momon.backend/
├── config/          # WebConfig.java (CORS), other configs
├── entity/          # JPA entities (User, Monster)
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic (MonsterService with AI integration)
├── controller/      # REST controllers
└── dto/             # Request/Response DTOs
```

### Frontend Directory Structure
```
frontend/
├── app/             # Next.js App Router pages
│   ├── layout.tsx   # Root layout
│   ├── page.tsx     # Main page (monster gallery)
│   ├── create/      # Monster creation page
│   └── result/      # Monster result display
├── lib/             # Utility functions
│   ├── deviceId.ts  # UUID generation and LocalStorage management
│   └── api.ts       # Axios instance with X-Device-ID interceptor
└── components/      # Reusable React components
```

## Development Best Practices

### Cost Optimization During Development
DALL-E API calls cost money. For development/testing:
- Comment out DALL-E integration in `MonsterService`
- Return a fixed S3 URL to a sample monster image
- Only enable real AI integration for final testing

### CORS Debugging
If getting CORS errors:
1. Verify `WebConfig.java` includes the frontend origin
2. Check browser DevTools Network tab for preflight OPTIONS request
3. Ensure `X-Device-ID` is in `allowedHeaders`

### Virtual Threads
Enabled in `application.yml` (`spring.threads.virtual.enabled: true`). Benefits:
- Better handling of concurrent AI API requests
- No need to manually configure thread pools
- Automatic for @Async and blocking I/O operations

## Git Workflow

### Branch Strategy
This project uses a feature-branch workflow based on Jira tickets:

1. **Main Branch**: `main` - production-ready code
2. **Feature Branches**: `feature/SCRUM-XX-short-description` - for each Jira ticket

### Working on a Jira Ticket

**Step 1: Create Feature Branch**
```bash
# Ensure you're on main and up to date
git checkout main
git pull origin main

# Create feature branch from Jira ticket
# Format: feature/SCRUM-XX-short-description
git checkout -b feature/SCRUM-7-monster-generation
```

**Step 2: Implement Changes**
- Make commits with descriptive messages referencing the Jira ticket
- Commit message format: `[SCRUM-XX] Description of changes`
- Example: `[SCRUM-7] Implement GPT-4o image analysis`

**Step 3: Verify Implementation**

For **Frontend** changes:
- Use Playwright MCP (`mcp__playwright__*` tools) to verify UI functionality
- Test critical user flows (navigation, form submission, image display)
- Ensure mobile responsiveness and cross-browser compatibility
- Only mark ticket as complete after automated verification passes

For **Backend** changes:
- Run unit tests: `./gradlew test`
- Test API endpoints manually or via integration tests
- Verify database schema changes (if any)
- Check logs for errors/warnings

**Step 4: Complete and Merge**
```bash
# Stage and commit final changes
git add .
git commit -m "[SCRUM-XX] Final implementation with tests"

# Switch to main and merge
git checkout main
git merge feature/SCRUM-XX-short-description

# Push to remote
git push origin main

# Delete feature branch (optional)
git branch -d feature/SCRUM-XX-short-description
```

**Step 5: Update Jira Ticket**
- Add a comment to the Jira ticket with:
  - Summary of implementation
  - Files changed
  - Test results (especially Playwright test results for frontend)
  - Any deployment notes or breaking changes
- Transition ticket to "Done" status

### Example Jira Comment Template
```
Implementation completed in branch feature/SCRUM-XX-short-description

Changes:
- [List of major changes]
- [Files modified]

Testing:
- ✅ Playwright tests passed (for frontend)
- ✅ Unit tests passed
- ✅ Manual testing completed

Merged to main: [commit hash]
```

## Jira Workflow

Project is tracked in Jira (https://momon2.atlassian.net):
- **Epic**: SCRUM-5 (Momon MVP)
- **Stories**: SCRUM-6 (User Auth), SCRUM-7 (Monster Gen), SCRUM-8 (Gallery)
- **Sub-tasks**: SCRUM-9 to SCRUM-16 (FE/BE implementation tasks)

### Jira Integration with Claude Code
When Claude Code is asked to work on a Jira ticket:
1. Automatically create a feature branch based on ticket number
2. Implement the required changes
3. For frontend: Run Playwright MCP tests to verify functionality
4. Commit changes with proper ticket reference
5. Merge to main when complete
6. Add implementation summary as comment to Jira ticket using MCP Atlassian tools

## Testing Strategy

### Backend Testing
- Unit tests for service layer (mock AI/S3 dependencies)
- Integration tests for API endpoints
- Use `@SpringBootTest` for full context testing

### Frontend Testing
- Component testing for UI elements
- Integration tests for API communication
- Manual testing for Device ID flow (check LocalStorage in DevTools)

## Deployment

- **Frontend**: Vercel (auto-deploy from Git)
- **Backend**: AWS EC2 (jar deployment)
- **Database**: PostgreSQL on production (switch via `application.yml` profile)
- **Storage**: AWS S3

## Additional Documentation

- `prd.md` - Full product requirements and user flows
- `docs/SETUP.md` - Detailed environment setup guide
- `README.md` - Project overview and quick start
