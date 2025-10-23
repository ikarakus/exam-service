#!/bin/bash

# Llama 3 Setup Script for English Guru Exam Service
# This script helps you set up and run Llama 3 locally using Ollama

set -e  # Exit on any error

echo "Setting up Llama 3 for English Guru Exam Service..."
echo "=================================================="

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if port is in use
port_in_use() {
    lsof -i :$1 >/dev/null 2>&1
}

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local max_attempts=30
    local attempt=1
    
    echo "Waiting for service to be ready..."
    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" >/dev/null 2>&1; then
            echo "Service is ready"
            return 0
        fi
        echo "Attempt $attempt/$max_attempts - waiting..."
        sleep 2
        attempt=$((attempt + 1))
    done
    echo "Service failed to start within expected time"
    return 1
}

# Check system requirements
echo "Checking system requirements..."

# Check OS (macOS and Linux only)
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "macOS detected"
    OS="macos"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo "Linux detected"
    OS="linux"
else
    echo "Unsupported OS: $OSTYPE"
    echo "This script supports macOS and Linux only"
    exit 1
fi

# Check available memory
echo "Checking system memory..."
if command_exists free; then
    # Linux
    total_mem=$(free -g | awk '/^Mem:/{print $2}')
    if [ "$total_mem" -lt 8 ]; then
        echo "Warning: Less than 8GB RAM detected ($total_mem GB)"
        echo "Llama 3 requires at least 8GB RAM for optimal performance"
        read -p "   Continue anyway? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    else
        echo "Sufficient memory detected ($total_mem GB)"
    fi
elif command_exists vm_stat; then
    # macOS
    total_mem=$(sysctl -n hw.memsize | awk '{print int($1/1024/1024/1024)}')
    if [ "$total_mem" -lt 8 ]; then
        echo "Warning: Less than 8GB RAM detected ($total_mem GB)"
        echo "Llama 3 requires at least 8GB RAM for optimal performance"
        read -p "   Continue anyway? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    else
        echo "Sufficient memory detected ($total_mem GB)"
    fi
else
    echo "Could not check memory. Please ensure you have at least 8GB RAM"
fi

# Check if curl is available
if ! command_exists curl; then
    echo "curl is not installed. Please install curl first"
    exit 1
fi
echo "curl is available"

# Check if Ollama is installed
echo "Checking Ollama installation..."
if ! command_exists ollama; then
    echo "Ollama is not installed"
    echo "Installing Ollama..."
    
    if [[ "$OS" == "macos" ]]; then
        # Check if Homebrew is available
        if command_exists brew; then
            echo "Using Homebrew to install Ollama..."
            brew install ollama
        else
            echo "Installing Ollama via official installer..."
            curl -fsSL https://ollama.ai/install.sh | sh
        fi
    else
        echo "Installing Ollama via official installer..."
        curl -fsSL https://ollama.ai/install.sh | sh
    fi
    
    # Verify installation
    if ! command_exists ollama; then
        echo " Ollama installation failed"
        echo "Please install manually from: https://ollama.ai/download"
        exit 1
    fi
    echo " Ollama installed successfully"
else
    echo " Ollama is already installed"
fi

# Check Ollama version
echo " Checking Ollama version..."
ollama_version=$(ollama --version 2>/dev/null || echo "unknown")
echo "Version: $ollama_version"

# Check if port 11434 is available
echo " Checking if Ollama port (11434) is available..."
if port_in_use 11434; then
    echo "Port 11434 is already in use"
    echo "This might mean Ollama is already running"
    
    # Test if it's actually Ollama
    if curl -s http://localhost:11434/api/tags >/dev/null 2>&1; then
        echo " Ollama service is already running on port 11434"
    else
        echo " Port 11434 is in use by another service"
        echo "Please stop the service using port 11434 or change Ollama configuration"
        exit 1
    fi
else
    echo " Port 11434 is available"
    
    # Start Ollama service
    echo " Starting Ollama service..."
    nohup ollama serve > /tmp/ollama.log 2>&1 &
    OLLAMA_PID=$!
    echo "Ollama started with PID: $OLLAMA_PID"
    
    # Wait for service to be ready
    if ! wait_for_service "http://localhost:11434/api/tags"; then
        echo " Failed to start Ollama service"
        echo "Check logs: tail -f /tmp/ollama.log"
        exit 1
    fi
fi

# Check available models
echo " Checking available models..."
existing_models=$(ollama list 2>/dev/null | grep -v "NAME" | awk '{print $1}' || echo "")
if [ -n "$existing_models" ]; then
    echo " Existing models found:"
    echo "$existing_models" | while read -r model; do
        echo "- $model"
    done
else
    echo "No models found"
fi

# Pull Llama 3 model if not already present
echo " Checking for Llama 3 model..."
if echo "$existing_models" | grep -q "llama3"; then
    echo " Llama 3 model is already available"
else
    echo " Pulling Llama 3 model (this may take a while)..."
    echo "This can take 5-15 minutes depending on your internet connection"
    
    if ollama pull llama3; then
        echo " Llama 3 model downloaded successfully"
    else
        echo " Failed to download Llama 3 model"
        echo "Check your internet connection and try again"
        exit 1
    fi
fi

# Verify the model is available
echo " Verifying Llama 3 model..."
if ollama list | grep -q "llama3"; then
    echo " Llama 3 model is ready"
    
    # Get model info
    model_info=$(ollama list | grep llama3)
    echo "Model info: $model_info"
else
    echo " Llama 3 model verification failed"
    exit 1
fi

# Test the model
echo " Testing Llama 3 model..."
echo "Sending test prompt..."

# Create a simple test prompt
test_prompt="Hello! Please respond with just 'Ready' if you can help with exam preparation for YDS, TOEFL, and IELTS."

# Run the test
echo "Prompt: $test_prompt"
test_response=$(ollama run llama3 "$test_prompt" 2>/dev/null | head -1 | tr -d '\n' || echo "")

if [ -n "$test_response" ]; then
    echo " Llama 3 model is working correctly"
    echo " Test response: $test_response"
else
    echo " Llama 3 model test failed - no response received"
    echo "This might be normal for the first run as the model loads"
    echo "You can test manually later with: ollama run llama3 'Hello'"
fi

# Final verification
echo " Final verification..."
if curl -s http://localhost:11434/api/tags >/dev/null 2>&1; then
    echo " Ollama API is responding"
else
    echo " Ollama API is not responding"
    echo "Check if Ollama is running: ps aux | grep ollama"
    echo "Check logs: tail -f /tmp/ollama.log"
fi

# Check if Spring Boot application can be built
echo " Checking Spring Boot application..."
if [ -f "pom.xml" ]; then
    echo " Maven project found"
    if command_exists mvn; then
        echo " Maven is available"
        echo " Testing Maven build..."
        if mvn compile -q >/dev/null 2>&1; then
            echo " Maven build successful"
        else
            echo "Maven build failed - check dependencies"
            echo "Run: mvn clean install"
        fi
    else
        echo " Maven is not installed"
        echo "Please install Maven to build the Spring Boot application"
    fi
else
    echo " pom.xml not found - make sure you're in the project directory"
fi

echo ""
echo " Llama 3 setup completed successfully!"
echo "=================================================="
echo ""
echo " Next steps:"
echo ""
echo "1. Start your Spring Boot application:"
echo "mvn spring-boot:run -Dspring.profiles.active=dev"
echo ""
echo "2. Test the Llama endpoint:"
echo "curl -X POST http://localhost:5004/api/llama/chat \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"prompt\": \"Help me with TOEFL reading comprehension\", \"examType\": \"TOEFL\"}'"
echo ""
echo "3. Check service health:"
echo "curl http://localhost:5004/api/llama/health"
echo ""
echo "4. Get exam help:"
echo "curl http://localhost:5004/api/llama/help/TOEFL"
echo ""
echo "Configuration:"
echo "- Ollama URL: http://localhost:11434"
echo "- Model: llama3"
echo "- Service Port: 5004"
echo "- API Base: http://localhost:5004/api/llama"
echo ""
echo "Available exam types: YDS, TOEFL, IELTS"
echo ""
echo "Troubleshooting:"
echo "- Ollama logs: tail -f /tmp/ollama.log"
echo "- Check Ollama status: ps aux | grep ollama"
echo "- Restart Ollama: pkill ollama && ollama serve"
echo "- Test model manually: ollama run llama3 'Hello'"
echo ""
echo "Documentation:"
echo "- See LLAMA_INTEGRATION.md for detailed usage"
echo "- API documentation: http://localhost:5004/swagger-ui.html"
echo ""
echo "Llama 3 setup completed successfully"
