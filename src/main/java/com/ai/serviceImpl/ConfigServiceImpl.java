package com.ai.serviceImpl;

import com.ai.entities.AppConfig;
import com.ai.repository.AppConfigRepository;
import com.ai.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl implements ConfigService {


    @Autowired
    private AppConfigRepository appConfigRepository;

    @Override
    public String getCredentials() {
        String openai_key = "";
        AppConfig keyConfig = appConfigRepository.findByKey("openai_key","system");
        try {
            if (keyConfig != null && keyConfig.getValue() != null) {
                openai_key = keyConfig.getValue();
            }
        } catch (Exception e) {
            System.out.println("Error parsing openai_key from app_config: " + e.getMessage());
        }
        return openai_key;
    }
}
