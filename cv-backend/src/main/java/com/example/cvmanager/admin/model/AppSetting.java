package com.example.cvmanager.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_setting")
public class AppSetting {

    @Id
    @Column(name = "setting_key", nullable = false)
    private String key;

    @Column(name = "setting_value", nullable = false, columnDefinition = "text")
    private String value;

    @Column(length = 500)
    private String description;

    protected AppSetting() {
    }

    public AppSetting(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }
}
