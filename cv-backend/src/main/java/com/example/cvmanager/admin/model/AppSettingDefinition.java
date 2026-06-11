package com.example.cvmanager.admin.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum AppSettingDefinition {
    APPLICATION_DISPLAY_NAME(
            "application.displayName",
            "Application display name",
            AppSettingValueType.STRING,
            "CV Manager",
            "Display name shown in the application UI."),
    AI_TOOLSET_ENABLED(
            "ai.toolsetEnabled",
            "AI toolset usage enabled",
            AppSettingValueType.BOOLEAN,
            "true",
            "Controls whether AI-assisted CV actions can run.");

    private final String key;
    private final String label;
    private final AppSettingValueType valueType;
    private final String defaultValue;
    private final String description;

    AppSettingDefinition(
            String key,
            String label,
            AppSettingValueType valueType,
            String defaultValue,
            String description) {
        this.key = key;
        this.label = label;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public String key() {
        return key;
    }

    public String label() {
        return label;
    }

    public AppSettingValueType valueType() {
        return valueType;
    }

    public String defaultValue() {
        return defaultValue;
    }

    public String description() {
        return description;
    }

    public static List<String> keys() {
        return Arrays.stream(values())
                .map(AppSettingDefinition::key)
                .toList();
    }

    public static Optional<AppSettingDefinition> fromKey(String key) {
        return Arrays.stream(values())
                .filter(setting -> setting.key.equals(key))
                .findFirst();
    }
}
