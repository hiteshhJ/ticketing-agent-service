# Ticketing Agent Service

AI-powered chatbot service for querying promotions and pricing data using Azure OpenAI.

## Features

- 🤖 Azure OpenAI GPT-4o integration with function calling
- 🔍 Query promotions and price history from MongoDB
- 🔒 JWT-based authentication from Next.js app
- ☕ Built with Spring Boot 3.2 & Java 21

## Technology Stack

- **Java 21**
- **Spring Boot 3.2**
- **Spring Data MongoDB**
- **Spring Security**
- **Azure OpenAI SDK**
- **Lombok** 
- **Maven**

## Prerequisites

- Java 21 or higher
- Maven 3.9+
- Azure OpenAI resource with GPT-4o deployment
- MongoDB Atlas (promotionDB & priceHistoryDB)

## Setup

### 1. Install Dependencies

```bash
cd agent-service-java
mvn clean install
```

### 2. Configure Environment

Create `.env` file or set environment variables:

```bash
# Azure OpenAI
export AZURE_OPENAI_ENDPOINT=https://YOUR-RESOURCE.openai.azure.com/
export AZURE_OPENAI_API_KEY=your-api-key
export AZURE_OPENAI_DEPLOYMENT_NAME=gpt-4o-deployment

# MongoDB
export MONGODB_PROMOTION_URI=MONGODB_PROMOTION_URI
export MONGODB_PRICE_HISTORY_URI=MONGODB_PRICE_HISTORY_URI

```

### 3. Run Development Server

```bash
mvn spring-boot:run
```

## API Endpoints

### `POST /chat`
Chat endpoint for querying data.

**Request:**
```json
{
  "message": "Show me active promotions for product 1381870",
  "conversationHistory": []
}
```

**Response:**
```json
{
  "response": "Found 2 active promotions...",
  "conversationHistory": ["..."]
}
```

### `GET /health`
Health check endpoint.

**Response:**
```json
{
  "status": "healthy",
  "service": "ticketing-agent-service"
}
```

## Available Tools

The agent can use these MongoDB query tools:

### Promotions
- `query_promotions` - Search promotions by product, offer type, dates
- `get_promotion_by_id` - Get specific promotion details
- `count_promotions` - Count promotions matching criteria

### Prices
- `query_prices` - Search price history
- `get_price_by_id` - Get specific price record
- `get_current_price` - Get current active price for a product

## Project Structure

```
src/main/java/com/sainsburys/agent/
├── AgentServiceApplication.java       # Main Spring Boot application
├── controller/
│   └── ChatController.java            # REST API endpoints
├── service/
│   ├── AgentService.java              # Azure OpenAI orchestration
│   └── ToolExecutor.java              # Tool execution logic
├── tools/
│   ├── PromotionTools.java            # Promotion queries
│   ├── PriceTools.java                # Price queries
│   └── ToolDefinitions.java           # Tool schemas
├── repository/
│   ├── PromotionRepository.java       # MongoDB - Promotions
│   └── PriceRepository.java           # MongoDB - Prices
├── model/
│   ├── ChatRequest.java               # Request DTO
│   ├── ChatResponse.java              # Response DTO
│   ├── Message.java                   # Message model
│   ├── EcsPromotion.java              # Promotion entity
│   └── EcsPrice.java                  # Price entity
├── security/
│   ├── JwtConfig.java                 # JWT validation filter
│   └── SecurityConfig.java            # Spring Security config
└── config/
    ├── AzureOpenAIConfig.java         # Azure OpenAI client
    └── MongoConfig.java               # MongoDB templates
```

## Docker

### Build Image
```bash
docker build -t ticketing-agent-service .
```

### Run Container
```bash
docker run -p 3001:3001 \
  -e AZURE_OPENAI_ENDPOINT=... \
  -e AZURE_OPENAI_API_KEY=... \
  -e AZURE_OPENAI_DEPLOYMENT_NAME=... \
  -e MONGODB_PROMOTION_URI=... \
  -e MONGODB_PRICE_HISTORY_URI=... \
  -e NEXTAUTH_SECRET=... \
  ticketing-agent-service
```

## Testing

### Run Unit Tests
```bash
mvn test
```

### Manual Testing
```bash
# Health check
curl http://localhost:8082/health

# Chat (requires JWT token)
curl -X POST http://localhost:8082/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "message": "What is the current price for SKU 1011289?"
  }'
```

## Deployment to Bosun

Add to your `bosun.yaml`:

```yaml
services:
  - name: ticketing-agent-service-java
    image: ${IMAGE_REGISTRY}/ticketing-agent-service:${VERSION}
    port: 3001
    env:
      - AZURE_OPENAI_ENDPOINT=${AZURE_OPENAI_ENDPOINT}
      - AZURE_OPENAI_API_KEY=${AZURE_OPENAI_API_KEY}
      - AZURE_OPENAI_DEPLOYMENT_NAME=${AZURE_OPENAI_DEPLOYMENT_NAME}
      - MONGODB_PROMOTION_URI=${MONGODB_PROMOTION_URI}
      - MONGODB_PRICE_HISTORY_URI=${MONGODB_PRICE_HISTORY_URI}
      - NEXTAUTH_SECRET=${NEXTAUTH_SECRET}
    healthcheck:
      path: /health
      interval: 30s
```

## Troubleshooting

### Service won't start

1. Verify Java 21 is installed: `java -version`
2. Check environment variables are set
3. Verify Azure OpenAI credentials
4. Test MongoDB connectivity

### No database results

1. Check MongoDB URIs point to correct databases
2. Verify collections exist (`ecsPromotions`, `ecsPrices`)
3. Test connectivity: `mongosh <MONGODB_URI>`