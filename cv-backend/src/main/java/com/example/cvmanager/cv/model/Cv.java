package com.example.cvmanager.cv.model;

import com.example.cvmanager.user.model.UserAccount;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "cv")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    @Setter
    private UserAccount owner;

    @Column(nullable = false)
    @Setter
    private String title;

//    @Column(name = "uploaded_html_file_path", nullable = false, length = 500)
//    @Setter
//    private String uploadedHtmlFilePath;

    @Lob
    @Column(name = "summary")
    @Setter
    private String summary;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @OneToOne(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CvPersonalDetails personalDetails;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CvEducationEntry> educationEntries = new ArrayList<>();

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CvWorkExperienceEntry> workExperienceEntries = new ArrayList<>();

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CvSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CvLanguage> languages = new ArrayList<>();

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CvLink> links = new ArrayList<>();

    public Cv(UserAccount owner, String title) {
        this.owner = owner;
        this.title = title;
        // this.uploadedHtmlFilePath = uploadedHtmlFilePath;
    }

    public void setPersonalDetails(CvPersonalDetails personalDetails) {
        if (this.personalDetails != null) {
            this.personalDetails.setCv(null);
        }
        this.personalDetails = personalDetails;
        if (personalDetails != null) {
            personalDetails.setCv(this);
        }
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void archive() {
        archivedAt = LocalDateTime.now();
    }

    public void restore() {
        archivedAt = null;
    }

    public boolean isArchived() {
        return archivedAt != null;
    }
}
