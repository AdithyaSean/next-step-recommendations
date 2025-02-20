# Next Step Recommendation Engine

## Overview
AI-powered recommendation service for the Next Step platform. Analyzes student data and generates career path recommendations.

## Features
- AI/ML models for career prediction
- Skills analysis
- Course compatibility checking
- Success probability calculation
- Real-time recommendations

## Tech Stack
- Spring Boot
- PostgreSQL

## ML Components
- Career path prediction models
- Skills assessment
- Academic performance analysis
- Industry trend analysis

## Setup
1. Configure environment variables:
   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/next-step-recommendations_db
   SPRING_DATASOURCE_USERNAME=next-step-recommendations
   SPRING_DATASOURCE_PASSWORD=secret
   GATEWAY_URL=http://localhost:8082
   ```

2. Start dependencies:
   ```bash
   docker-compose up -d
   ```

3. Start the service:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Documentation
See [docs/api.md](docs/recommendation-microservice-api) for detailed API documentation.

## ML Model Documentation
See [docs/ml-models.md](docs/ml-models.md) for information about the ML models.

## Development Guide
See [docs/development.md](docs/development.md) for development guidelines.
