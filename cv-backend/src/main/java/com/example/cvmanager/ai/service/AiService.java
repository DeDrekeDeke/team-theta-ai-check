package com.example.cvmanager.ai.service;

import com.example.cvmanager.ai.model.AiActionType;

public interface AiService {

    String suggest(AiActionType actionType, String input);
}
