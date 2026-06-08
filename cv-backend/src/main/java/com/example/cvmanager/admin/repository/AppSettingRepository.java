package com.example.cvmanager.admin.repository;

import com.example.cvmanager.admin.model.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingRepository extends JpaRepository<AppSetting, String> {
}
