FROM node:20-alpine AS base

# Development stage
FROM base AS development

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

EXPOSE 3000

# Run in development mode with hot reload
CMD ["npm", "run", "dev"]
