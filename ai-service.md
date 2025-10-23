# English Guru AI Service - GPT/Chat API Documentation

## Overview

The English Guru AI Service provides a comprehensive set of AI-powered endpoints for language learning, including chat functionality, text-to-speech, video generation, translation, and assessment capabilities. The service is built with Spring Boot and integrates with OpenAI's GPT models.

## Base URL

```
http://localhost:5003/api/gpt
```

## API Endpoints

### 1. Chat Endpoint

**URL:** `POST /gpt/chat`

**Description:** Main chat endpoint for conversational AI interactions with customizable language learning parameters.

**Request Body:**
```json
{
  "model": "gpt-4o-mini",
  "prompt": "Hello, how are you today?",
  "language": "en",
  "languageLevel": "intermediate",
  "topic": "daily_conversation",
  "tutor": "friendly_teacher",
  "pastDialogue": [
    {
      "senderNickname": "user",
      "receiverNickname": "tutor",
      "message": "Previous message",
      "timeSent": "2024-01-01T10:00:00Z",
      "senderType": "user",
      "timeOrder": "1"
    }
  ],
  "firstMessage": false,
  "userId": 123,
  "tutorId": 456
}
```

**Request Parameters:**
- `model` (String): AI model to use ("gpt-4o", "gpt-4o-mini", or "auto")
- `prompt` (String): User's message/input
- `language` (String): Target language for learning (default: "any")
- `languageLevel` (String): User's proficiency level (default: "any")
- `topic` (String): Conversation topic (default: "any")
- `tutor` (String): Tutor personality/type (default: "any")
- `pastDialogue` (Array): Previous conversation history
- `firstMessage` (Boolean): Whether this is the first message in conversation
- `userId` (Long): User ID for personalization
- `tutorId` (Long): Tutor ID for specific tutor selection

**Response:**
```json
{
  "resultCode": 200,
  "errorMessage": null,
  "responseId": null,
  "responseBody": [
    {
      "choices": [
        {
          "index": 0,
          "message": {
            "role": "assistant",
            "content": "Hello! I'm doing great, thank you for asking. How about you? What would you like to practice today?"
          }
        }
      ],
      "language": "en",
      "languageLevel": "intermediate"
    }
  ]
}
```

### 2. Get Tutors Endpoint

**URL:** `GET /gpt/tutors/{lang}/{kids}`

**Description:** Retrieves available tutors based on language and age group.

**Path Parameters:**
- `lang` (String): Language code (e.g., "en", "tr")
- `kids` (String): "true" for child-friendly tutors, "false" for adult tutors

**Response:**
```json
{
  "resultCode": 200,
  "errorMessage": null,
  "responseId": null,
  "responseBody": [
    {
      "id": 1,
      "nickName": "Friendly Teacher",
      "description": "A patient and encouraging tutor",
      "isKids": true,
      "language": "en"
    }
  ]
}
```

### 3. Language Level Evaluation

**URL:** `POST /gpt/language_level`

**Description:** Evaluates user's language proficiency level based on conversation.

**Request Body:**
```json
{
  "language": "en",
  "conversation": [
    {
      "role": "user",
      "content": "Hello, how are you?"
    },
    {
      "role": "assistant", 
      "content": "I'm fine, thank you!"
    }
  ]
}
```

**Response:**
```json
{
  "resultCode": 200,
  "errorMessage": null,
  "responseId": null,
  "responseBody": ["B1"]
}
```

### 4. Text-to-Speech

**URL:** `POST /gpt/tts`

**Description:** Converts text to speech and returns audio file.

**Request Body:**
```json
{
  "text": "Hello, welcome to English Guru!",
  "voice": "alloy",
  "model": "tts-1"
}
```

**Response:** Binary audio file (MP3 format)

### 5. Video with TTS

**URL:** `POST /gpt/video-tts`

**Description:** Generates animated video with synchronized speech.

**Request Body:**
```json
{
  "text": "Hello, let's learn English together!",
  "voice": "alloy",
  "model": "tts-1",
  "avatarId": 1
}
```

**Response:** Binary video file (MP4 format)

### 6. Idle Video Generation

**URL:** `POST /gpt/video-idle`

**Description:** Generates idle animation video for character.

**Request Body:** None

**Response:** Binary video file (MP4 format)

### 7. Speech-to-Text

**URL:** `POST /gpt/stt`

**Description:** Transcribes audio to text.

**Request Body:**
```json
{
  "audioData": "base64_encoded_audio",
  "model": "whisper-1"
}
```

**Response:**
```json
{
  "resultCode": 200,
  "errorMessage": null,
  "responseId": null,
  "responseBody": ["Hello, this is a test transcription"]
}
```

### 8. Translation

**URL:** `POST /gpt/translate`

**Description:** Translates text between languages.

**Request Body:**
```json
{
  "inputLanguage": "en",
  "outputLanguage": "tr",
  "message": "Hello, how are you?"
}
```

**Response:**
```json
{
  "resultCode": 200,
  "errorMessage": null,
  "responseId": null,
  "responseBody": ["Merhaba, nasılsın?"]
}
```

### 9. Test Generation

**URL:** `POST /gpt/test`

**Description:** Generates language assessment questions.

**Request Body:**
```json
{
  "userId": 123,
  "courseLang": "en",
  "selectedLevel": "intermediate",
  "selectedTopic": "grammar",
  "selectedLevelId": 2,
  "selectedTopicId": 5
}
```

**Response:**
```json
{
  "questions": [
    {
      "id": 1,
      "question": "Choose the correct form:",
      "options": ["I am", "I is", "I are"],
      "correctAnswer": 0
    }
  ]
}
```

### 10. Get Credentials

**URL:** `GET /gpt/credentials`

**Description:** Retrieves API credentials (for internal use).

**Response:**
```json
{
  "resultCode": 200,
  "errorMessage": null,
  "responseId": null,
  "responseBody": ["sk-..."]
}
```

## Code Structure

### Controller Layer
- **ChatGptController**: Main REST controller handling all GPT-related endpoints
- **Location**: `src/main/java/com/ai/controller/ChatGptController.java`

### Service Layer
- **OpenAiService**: Interface defining AI service operations
- **OpenAiServiceImpl**: Implementation of OpenAI API interactions
- **TtsService**: Text-to-speech functionality
- **VideoTtsService**: Video generation with TTS
- **IdleVideoService**: Idle video generation
- **ExamService**: Test and assessment generation
- **AvatarService**: Tutor/avatar management

### DTO Layer
- **Chat**: Main chat request DTO
- **ChatResponse**: Chat response structure
- **ResponseDto**: Generic response wrapper
- **MessageDto**: Message structure for conversation history
- **TranslationRequestDto**: Translation request structure
- **TestRequestDto**: Test generation request structure

### Configuration
- **OpenAIConfig**: OpenAI API configuration and model settings
- **Location**: `src/main/java/com/ai/config/OpenAIConfig.java`

### Key Features

1. **Model Selection**: Automatic model selection based on prompt complexity
2. **Personalization**: User-specific responses based on profile data
3. **Tutor Customization**: Different tutor personalities and child-friendly options
4. **Language Detection**: Automatic language detection for conversations
5. **Context Management**: Conversation history and context preservation
6. **Multi-modal Support**: Text, audio, and video generation capabilities

### Configuration Files

- **application.yml**: Main configuration
- **application-dev.yml**: Development environment settings
- **application-prod.yml**: Production environment settings

### OpenAI Integration

The service integrates with OpenAI's API using:
- **Chat Completions API**: For conversational AI
- **TTS API**: For text-to-speech
- **Whisper API**: For speech-to-text
- **Models**: GPT-4o and GPT-4o-mini

### Error Handling

All endpoints return standardized error responses with:
- `resultCode`: HTTP status code
- `errorMessage`: Descriptive error message
- `responseBody`: Empty or null on errors

### Authentication

API credentials are managed through:
- Database storage for dynamic key management
- Automatic token injection via RestTemplate interceptors
- Environment-specific configuration

## Usage Examples

### Basic Chat
```bash
curl -X POST http://localhost:5003/api/gpt/chat \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4o-mini",
    "prompt": "Hello, I want to practice English",
    "language": "en",
    "languageLevel": "beginner"
  }'
```

### Translation
```bash
curl -X POST http://localhost:5003/api/gpt/translate \
  -H "Content-Type: application/json" \
  -d '{
    "inputLanguage": "en",
    "outputLanguage": "tr", 
    "message": "Good morning"
  }'
```

### Get Tutors
```bash
curl -X GET http://localhost:5003/api/gpt/tutors/en/true
```

## Development Setup

1. **Prerequisites**: Java 11+, Maven, PostgreSQL
2. **Configuration**: Update `application-dev.yml` with your database and OpenAI credentials
3. **Run**: `mvn spring-boot:run -Dspring.profiles.active=dev`
4. **Port**: Service runs on port 5003 by default

## Notes

- All timestamps are in ISO 8601 format
- Audio/video responses are returned as binary data
- The service supports multiple languages and proficiency levels
- User personalization is based on stored profile data
- Tutor selection affects conversation style and content appropriateness
