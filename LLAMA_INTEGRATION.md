# Llama 3 Integration for English Guru Exam Service

This document describes the Llama 3 integration for exam-related chat functionality, specifically designed for YDS, TOEFL, and IELTS exam preparation.

## Overview

The Llama 3 integration provides a specialized chat endpoint that:
- Uses Llama 3 model instead of OpenAI GPT
- Focuses specifically on exam-related topics (YDS, TOEFL, IELTS)
- Integrates with the existing question bank
- Provides exam-specific responses and context

## Architecture

### Components

1. **LlamaController** - REST endpoints for Llama chat
2. **LlamaService** - Service interface for Llama operations
3. **LlamaServiceImpl** - Implementation with Ollama integration
4. **LlamaConfig** - Configuration for Llama service
5. **DTOs** - Request/Response objects for Llama chat

### DTOs

- `LlamaChatRequest` - Input request with exam type, prompt, and parameters
- `LlamaChatResponse` - Response with exam-specific information
- `LlamaApiRequest/Response` - Internal API communication objects

## Setup Instructions

### 1. Install Ollama

```bash
# macOS
brew install ollama

# Linux
curl -fsSL https://ollama.ai/install.sh | sh

# Windows
# Download from https://ollama.ai/download
```

### 2. Run Setup Script

```bash
./setup-llama.sh
```

This script will:
- Check if Ollama is installed
- Start Ollama service
- Pull Llama 3 model
- Test the model
- Provide next steps

### 3. Manual Setup (Alternative)

```bash
# Start Ollama service
ollama serve

# Pull Llama 3 model
ollama pull llama3

# Test the model
ollama run llama3 "Hello, are you ready to help with exam preparation?"
```

## API Endpoints

### Base URL
```
http://localhost:5004/api/llama
```

### 1. Chat Endpoint

**POST** `/llama/chat`

Chat with Llama 3 for exam-related topics.

**Request Body:**
```json
{
  "prompt": "Help me with TOEFL reading comprehension strategies",
  "examType": "TOEFL",
  "language": "en",
  "difficulty": "intermediate",
  "topic": "reading",
  "pastDialogue": [
    {
      "senderNickname": "user",
      "receiverNickname": "tutor",
      "message": "I need help with reading comprehension",
      "timeSent": "2024-01-01T10:00:00Z",
      "senderType": "user",
      "timeOrder": "1"
    }
  ],
  "userId": 123,
  "includeQuestionBank": true
}
```

**Response:**
```json
{
  "resultCode": 200,
  "errorMessage": null,
  "responseId": null,
  "responseBody": [
    {
      "response": "For TOEFL reading comprehension, here are key strategies...",
      "examType": "TOEFL",
      "language": "en",
      "difficulty": "intermediate",
      "topic": "reading",
      "relatedQuestions": [
        {
          "questionId": 1,
          "questionText": "What is the main idea of the passage?",
          "examType": "TOEFL",
          "topic": "reading",
          "difficulty": "intermediate",
          "relevance": "high"
        }
      ],
      "modelUsed": "llama3",
      "responseTime": 1250
    }
  ]
}
```

### 2. Health Check

**GET** `/llama/health`

Check if Llama service is available.

**Response:**
```json
{
  "resultCode": 200,
  "errorMessage": null,
  "responseId": null,
  "responseBody": ["Llama service is available"]
}
```

### 3. Available Models

**GET** `/llama/models`

Get list of available models.

**Response:**
```json
{
  "resultCode": 200,
  "errorMessage": null,
  "responseId": null,
  "responseBody": ["llama3"]
}
```

### 4. Exam Help

**GET** `/llama/help/{examType}`

Get exam-specific help information.

**Examples:**
- `/llama/help/YDS`
- `/llama/help/TOEFL`
- `/llama/help/IELTS`

## Configuration

### Application Properties

```yaml
# application.yml
llama:
  base-url: "http://localhost:11434"
  model: "llama3"
  timeout: 30000
  max-retries: 3
```

### Environment Variables (Optional)

```bash
export LLAMA_BASE_URL="http://localhost:11434"
export LLAMA_MODEL="llama3"
export LLAMA_TIMEOUT="30000"
```

## Exam Types Supported

### YDS (Yabancı Dil Bilgisi Seviye Tespit Sınavı)
- Turkish-English translation
- Grammar and vocabulary
- Reading comprehension
- Cloze test questions

### TOEFL (Test of English as a Foreign Language)
- Academic English skills
- Reading, Listening, Speaking, Writing
- Integrated tasks
- Academic vocabulary and grammar

### IELTS (International English Language Testing System)
- General and Academic modules
- All four skills: Reading, Writing, Listening, Speaking
- Task 1 and Task 2 writing
- Academic and general vocabulary

## Features

### 1. Exam-Specific Context
- System prompts tailored to each exam type
- Specialized vocabulary and grammar focus
- Exam format awareness

### 2. Question Bank Integration
- Retrieves relevant questions from database
- Provides context-aware question references
- Links responses to existing question bank

### 3. Difficulty Adaptation
- Adjusts response complexity based on user level
- Beginner: Shorter, simpler responses
- Advanced: Detailed, comprehensive responses

### 4. Topic Focus
- Grammar, vocabulary, reading, listening, writing, speaking
- Context-aware responses based on topic
- Specialized strategies for each topic

## Usage Examples

### Basic Chat
```bash
curl -X POST http://localhost:5004/api/llama/chat \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Explain the difference between present perfect and past simple",
    "examType": "IELTS",
    "topic": "grammar"
  }'
```

### YDS Translation Help
```bash
curl -X POST http://localhost:5004/api/llama/chat \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "How do I translate this Turkish sentence to English: \u0027Bugün hava çok güzel\u0027",
    "examType": "YDS",
    "topic": "translation"
  }'
```

### TOEFL Writing Practice
```bash
curl -X POST http://localhost:5004/api/llama/chat \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Help me write a TOEFL independent essay about technology",
    "examType": "TOEFL",
    "topic": "writing",
    "difficulty": "advanced"
  }'
```

## Troubleshooting

### Common Issues

1. **Ollama not running**
   ```bash
   ollama serve
   ```

2. **Model not found**
   ```bash
   ollama pull llama3
   ```

3. **Connection timeout**
   - Check if Ollama is running on port 11434
   - Verify firewall settings
   - Increase timeout in configuration

4. **Empty responses**
   - Check Ollama logs: `ollama logs`
   - Verify model is properly loaded
   - Test with simple prompts first

### Logs

Check application logs for detailed error information:
```bash
tail -f logs/application.log
```

### Health Check

Always verify service health before testing:
```bash
curl http://localhost:5004/api/llama/health
```

## Performance Considerations

### Response Times
- Typical response time: 1-3 seconds
- Depends on prompt complexity and system resources
- Consider timeout settings for production

### Resource Usage
- Llama 3 requires significant RAM (8GB+ recommended)
- GPU acceleration available with CUDA
- Monitor system resources during usage

### Scaling
- For production, consider Ollama clustering
- Load balancing for multiple instances
- Caching for frequently asked questions

## Security Notes

- Llama runs locally, no external API calls
- No sensitive data sent to external services
- Consider rate limiting for production use
- Validate and sanitize all inputs

## Development

### Adding New Exam Types

1. Update `LlamaServiceImpl.buildSystemPrompt()`
2. Add exam type validation in `LlamaController`
3. Update help text in `LlamaController.getExamHelpText()`
4. Add test cases for new exam type

### Customizing Responses

1. Modify system prompts in `LlamaServiceImpl`
2. Adjust parameters in `configureRequestParameters()`
3. Enhance question bank integration
4. Add exam-specific context

## Testing

### Unit Tests
```bash
mvn test -Dtest=LlamaServiceTest
```

### Integration Tests
```bash
mvn test -Dtest=LlamaControllerIntegrationTest
```

### Manual Testing
Use the provided curl examples or test with Postman/Insomnia.

## Support

For issues or questions:
1. Check this documentation
2. Review application logs
3. Test with simple prompts first
4. Verify Ollama installation and configuration

