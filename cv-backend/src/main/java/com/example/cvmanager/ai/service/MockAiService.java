package com.example.cvmanager.ai.service;

import com.example.cvmanager.ai.model.AiActionType;
import org.springframework.stereotype.Service;

@Service
public class MockAiService implements AiService {

    @Override
    public String suggest(AiActionType actionType, String input) {
        return "Mock " + actionType.name().toLowerCase().replace('_', ' ')
                + " suggestion for content from: " + input;
    }
}
