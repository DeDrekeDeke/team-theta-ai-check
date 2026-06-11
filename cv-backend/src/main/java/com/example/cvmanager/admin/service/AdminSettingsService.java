package com.example.cvmanager.admin.service;

import com.example.cvmanager.admin.dto.AppSettingResponse;
import com.example.cvmanager.admin.dto.AppSettingUpdateRequest;
import com.example.cvmanager.admin.model.AppSetting;
import com.example.cvmanager.admin.model.AppSettingDefinition;
import com.example.cvmanager.admin.model.AppSettingValueType;
import com.example.cvmanager.admin.repository.AppSettingRepository;
import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.common.exception.NotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminSettingsService {

    private final AppSettingRepository appSettingRepository;

    public AdminSettingsService(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    @Transactional(readOnly = true)
    public List<AppSettingResponse> listSettings() {
        Map<String, AppSetting> settingsByKey = appSettingRepository.findAllById(AppSettingDefinition.keys()).stream()
                .collect(Collectors.toMap(AppSetting::getKey, Function.identity()));

        return Arrays.stream(AppSettingDefinition.values())
                .map(definition -> toResponse(definition, settingsByKey.get(definition.key())))
                .toList();
    }

    @Transactional
    public AppSettingResponse updateSetting(String key, AppSettingUpdateRequest request) {
        AppSettingDefinition definition = AppSettingDefinition.fromKey(key)
                .orElseThrow(() -> new NotFoundException("Setting not found", "SETTING_NOT_FOUND"));
        AppSetting setting = appSettingRepository.findById(key)
                .orElseGet(() -> new AppSetting(
                        definition.key(),
                        definition.defaultValue(),
                        definition.description()));

        setting.setValue(normalizeValue(definition, request.value()));
        return toResponse(definition, appSettingRepository.save(setting));
    }

    @Transactional(readOnly = true)
    public String getApplicationDisplayName() {
        return getValue(AppSettingDefinition.APPLICATION_DISPLAY_NAME);
    }

    @Transactional(readOnly = true)
    public boolean isAiToolsetEnabled() {
        return Boolean.parseBoolean(getValue(AppSettingDefinition.AI_TOOLSET_ENABLED));
    }

    private String getValue(AppSettingDefinition definition) {
        return appSettingRepository.findById(definition.key())
                .map(AppSetting::getValue)
                .map(value -> normalizeValue(definition, value))
                .orElse(definition.defaultValue());
    }

    private String normalizeValue(AppSettingDefinition definition, String value) {
        if (value == null) {
            throw new BadRequestException("Value is required", "SETTING_INVALID_VALUE");
        }

        String trimmedValue = value.trim();
        if (definition.valueType() == AppSettingValueType.BOOLEAN) {
            if ("true".equalsIgnoreCase(trimmedValue) || "false".equalsIgnoreCase(trimmedValue)) {
                return trimmedValue.toLowerCase();
            }
            throw new BadRequestException("Value must be true or false", "SETTING_INVALID_VALUE");
        }

        if (trimmedValue.isBlank()) {
            throw new BadRequestException("Value is required", "SETTING_INVALID_VALUE");
        }
        if (definition == AppSettingDefinition.APPLICATION_DISPLAY_NAME && trimmedValue.length() > 100) {
            throw new BadRequestException("Display name must be 100 characters or fewer", "SETTING_INVALID_VALUE");
        }

        return trimmedValue;
    }

    private AppSettingResponse toResponse(AppSettingDefinition definition, AppSetting setting) {
        String value = setting == null ? definition.defaultValue() : normalizeValue(definition, setting.getValue());
        return new AppSettingResponse(
                definition.key(),
                value,
                definition.valueType().name(),
                definition.label(),
                definition.description());
    }
}
