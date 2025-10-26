package com.exam.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class OllamaStartupHook {
    
    private static final Logger logger = LoggerFactory.getLogger(OllamaStartupHook.class);
    private static final String OLLAMA_URL = "http://localhost:11434/api/tags";
    private Process ollamaProcess;
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("🚀 Application started, checking Ollama service...");
        
        if (isOllamaRunning()) {
            logger.info("✅ Ollama is already running");
        } else {
            logger.info("🔄 Starting Ollama service...");
            startOllama();
        }
        
        // Check if llama3 model is available
        checkLlamaModel();
    }
    
    private boolean isOllamaRunning() {
        try {
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void startOllama() {
        try {
            // Start Ollama in background
            ProcessBuilder processBuilder = new ProcessBuilder("ollama", "serve");
            processBuilder.redirectErrorStream(true);
            ollamaProcess = processBuilder.start();
            
            // Wait for Ollama to start
            logger.info("⏳ Waiting for Ollama to start...");
            for (int i = 0; i < 30; i++) {
                Thread.sleep(1000);
                if (isOllamaRunning()) {
                    logger.info("✅ Ollama started successfully");
                    return;
                }
                logger.info("Waiting for Ollama... ({}/30)", i + 1);
            }
            
            logger.error("❌ Failed to start Ollama after 30 seconds");
        } catch (Exception e) {
            logger.error("❌ Error starting Ollama: {}", e.getMessage());
        }
    }
    
    private void checkLlamaModel() {
        try {
            // Check if llama3 model is available
            ProcessBuilder processBuilder = new ProcessBuilder("ollama", "list");
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                String result = output.toString();
                if (result.contains("llama3")) {
                    logger.info("✅ llama3 model is available");
                } else {
                    logger.info("📥 llama3 model not found, downloading...");
                    downloadLlamaModel();
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error checking llama3 model: {}", e.getMessage());
        }
    }
    
    private void downloadLlamaModel() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ollama", "pull", "llama3");
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("📥 {}", line);
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("✅ llama3 model downloaded successfully");
            } else {
                logger.error("❌ Failed to download llama3 model");
            }
        } catch (Exception e) {
            logger.error("❌ Error downloading llama3 model: {}", e.getMessage());
        }
    }
}
