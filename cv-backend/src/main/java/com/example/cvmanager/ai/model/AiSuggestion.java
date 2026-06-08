package com.example.cvmanager.ai.model;

import com.example.cvmanager.cv.model.Cv;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_suggestion")
public class AiSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cv_id", nullable = false)
    private Cv cv;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 100)
    private AiActionType actionType;

    @Column(name = "original_text", nullable = false, columnDefinition = "text")
    private String originalText;

    @Column(name = "suggested_text", nullable = false, columnDefinition = "text")
    private String suggestedText;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AiSuggestion() {
    }

    public AiSuggestion(Cv cv, AiActionType actionType, String originalText, String suggestedText, String status) {
        this.cv = cv;
        this.actionType = actionType;
        this.originalText = originalText;
        this.suggestedText = suggestedText;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Cv getCv() {
        return cv;
    }

    public AiActionType getActionType() {
        return actionType;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getSuggestedText() {
        return suggestedText;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
