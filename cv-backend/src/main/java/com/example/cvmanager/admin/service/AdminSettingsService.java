package com.example.cvmanager.admin.service;

import com.example.cvmanager.admin.dto.AppSettingResponse;
import com.example.cvmanager.admin.dto.AppSettingUpdateRequest;
import com.example.cvmanager.admin.model.AppSetting;
import com.example.cvmanager.admin.repository.AppSettingRepository;
import com.example.cvmanager.common.exception.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
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
        return appSettingRepository.findAll(Sort.by("key")).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AppSettingResponse updateSetting(String key, AppSettingUpdateRequest request) {
        AppSetting setting = appSettingRepository.findById(key)
                .orElseThrow(() -> new NotFoundException("Setting not found", "SETTING_NOT_FOUND"));

        setting.setValue(request.value());
        return toResponse(appSettingRepository.save(setting));
    }

    private AppSettingResponse toResponse(AppSetting setting) {
        return new AppSettingResponse(setting.getKey(), setting.getValue(), setting.getDescription());
    }
}
