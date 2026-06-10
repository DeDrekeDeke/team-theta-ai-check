package com.example.cvmanager.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cv_work_experience_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CvWorkExperienceEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cv_id", nullable = false)
    @Setter
    private Cv cv;

    @Column(name = "employer", nullable = false)
    @Setter
    private String employer;

    @Column(name = "job_title", nullable = false)
    @Setter
    private String jobTitle;

    @Column(name = "location")
    @Setter
    private String location;

    @Column(name = "start_date")
    @Setter
    private LocalDate startDate;

    @Column(name = "end_date")
    @Setter
    private LocalDate endDate;

    @Lob
    @Column(name = "description")
    @Setter
    private String description;

    @Column(name = "display_order", nullable = false)
    @Setter
    private int displayOrder;

    public CvWorkExperienceEntry(Cv cv, String employer, String jobTitle) {
        this.cv = cv;
        this.employer = employer;
        this.jobTitle = jobTitle;
    }
}
