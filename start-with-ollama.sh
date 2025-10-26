#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Starting English Guru Exam Service with Ollama...${NC}"

# Check if Ollama is installed
if ! command -v ollama &> /dev/null; then
    echo -e "${RED}❌ Ollama is not installed. Please install Ollama first.${NC}"
    echo -e "${YELLOW}Install with: brew install ollama${NC}"
    exit 1
fi

# Check if Ollama is already running
if curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Ollama is already running${NC}"
else
    echo -e "${YELLOW}🔄 Starting Ollama service...${NC}"
    # Start Ollama in background
    ollama serve &
    OLLAMA_PID=$!
    
    # Wait for Ollama to start
    echo -e "${YELLOW}⏳ Waiting for Ollama to start...${NC}"
    for i in {1..30}; do
        if curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Ollama started successfully${NC}"
            break
        fi
        sleep 1
        echo -n "."
    done
    
    if ! curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
        echo -e "${RED}❌ Failed to start Ollama after 30 seconds${NC}"
        kill $OLLAMA_PID 2>/dev/null
        exit 1
    fi
fi

# Check if llama3 model is available
echo -e "${YELLOW}🔍 Checking for llama3 model...${NC}"
if ! ollama list | grep -q "llama3"; then
    echo -e "${YELLOW}📥 Downloading llama3 model (this may take a while)...${NC}"
    ollama pull llama3
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ llama3 model downloaded successfully${NC}"
    else
        echo -e "${RED}❌ Failed to download llama3 model${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✅ llama3 model is available${NC}"
fi

# Set Java environment
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-8.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

echo -e "${BLUE}🎯 Starting Spring Boot application...${NC}"

# Function to cleanup on exit
cleanup() {
    echo -e "\n${YELLOW}🛑 Shutting down services...${NC}"
    if [ ! -z "$OLLAMA_PID" ]; then
        kill $OLLAMA_PID 2>/dev/null
        echo -e "${GREEN}✅ Ollama stopped${NC}"
    fi
    exit 0
}

# Set trap to cleanup on script exit
trap cleanup SIGINT SIGTERM

# Start Spring Boot application
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Cleanup when Spring Boot exits
cleanup
