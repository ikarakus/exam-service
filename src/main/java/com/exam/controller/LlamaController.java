package com.exam.controller;

import com.exam.dto.*;
import com.exam.entities.*;
import com.exam.repository.*;
import com.exam.service.*;
import com.exam.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/llama")
public class LlamaController {
    
    @Autowired
    private LlamaService llamaService;

    @Autowired
    AvatarService avatarService;

    @Autowired
    UserService userService;

    @Autowired
    AvatarProfileRepository avatarProfileRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    /**
     * Chat endpoint for exam-related topics using Llama 3
     * Handles the same request/response format as the previous GPT chat
     */
    @PostMapping("/chat")
    public ResponseEntity<ResponseDto> chat(@RequestBody Chat chat) {
        ResponseDto responseDto = new ResponseDto<>();
        
        // Handle database lookups for personalization
        String tutorName = chat.getTutor();
        String userNickname = null;
        String ageRange = null;
        boolean isChildFriendly = false;
        
        // Lookup tutor information if tutorId is provided
        if (chat.getTutorId() != null) {
            try {
                Optional<AvatarProfile> avatarProfile = avatarProfileRepository.findByTutorId(chat.getTutorId());
                if (avatarProfile.isPresent()) {
                    tutorName = avatarProfile.get().getFullName();
                    isChildFriendly = avatarProfile.get().isKids();
                }
            } catch (Exception e) {
                // Log error but continue with original tutor name
                System.err.println("Error looking up tutor: " + e.getMessage());
            }
        }
        
        // Lookup user information if userId is provided
        if (chat.getUserId() != null) {
            try {
                Optional<User> user = userService.getUser(chat.getUserId());
                if (user.isPresent()) {
                    Optional<UserProfile> userProfile = userProfileRepository.findByUser(user.get());
                    if (userProfile.isPresent()) {
                        userNickname = userProfile.get().getNickName();
                        ageRange = getAgeRange(userProfile.get().getAgeGroup());
                    }
                }
            } catch (Exception e) {
                // Log error but continue without user personalization
                System.err.println("Error looking up user: " + e.getMessage());
            }
        }
        
        ChatResponse chatResponse = llamaService.callLlamaApi(
            chat.getModel(), 
            chat.getPrompt(), 
            chat.getLanguage(), 
            chat.getLanguageLevel(), 
            chat.getTopic(), 
            tutorName, 
            chat.getPastDialogue(),
            isChildFriendly,
            userNickname,
            ageRange,
            chat.getFirstMessage() != null ? chat.getFirstMessage() : false
        );
        
        responseDto.setResponseBody(Collections.singletonList(chatResponse));
        Helper.fillResponse(responseDto, ResultCodes.OK, null);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    private String getAgeRange(Integer ageGroup) {
        if (ageGroup == null) return null;
        
        switch (ageGroup) {
            case 1: return "5-12 years";
            case 2: return "13-17 years";
            case 3: return "18-25 years";
            case 4: return "26-35 years";
            case 5: return "36-50 years";
            case 6: return "51+ years";
            default: return null;
        }
    }

    @GetMapping("/tutors/{lang}/{kids}")
    public ResponseEntity<ResponseDto> getTutors(@PathVariable String lang, @PathVariable String kids) {
        ResponseDto responseDto = new ResponseDto<>();
        List<AvatarProfileDto> avatarProfileDtoList = avatarService.getTutors(lang,Boolean.parseBoolean(kids));
        responseDto.setResponseBody(avatarProfileDtoList);
        Helper.fillResponse(responseDto, ResultCodes.OK, null);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * Check if Ollama is running and start it if not
     * This endpoint can be used on server to ensure Ollama is running
     */
    @GetMapping("/ollama/status")
    public ResponseEntity<Map<String, Object>> checkOllamaStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if Ollama is running
            boolean isRunning = isOllamaRunning();
            response.put("running", isRunning);
            response.put("message", isRunning ? "Ollama is running" : "Ollama is not running");
            
            if (!isRunning) {
                // Try to start Ollama
                boolean started = startOllama();
                response.put("started", started);
                response.put("message", started ? "Ollama started successfully" : "Failed to start Ollama");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("running", false);
            response.put("started", false);
            response.put("error", e.getMessage());
            response.put("message", "Error checking Ollama status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check if Ollama service is running
     */
    private boolean isOllamaRunning() {
        try {
            URL url = new URL("http://localhost:11434/api/tags");
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

    /**
     * Start Ollama service
     */
    private boolean startOllama() {
        try {
            // Start Ollama in background
            ProcessBuilder processBuilder = new ProcessBuilder("ollama", "serve");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // Wait a bit for Ollama to start
            Thread.sleep(3000);
            
            // Check if it's running now
            return isOllamaRunning();
            
        } catch (Exception e) {
            System.err.println("Error starting Ollama: " + e.getMessage());
            return false;
        }
    }
}

