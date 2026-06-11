package com.example.cvmanager.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cv_education_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CvEducationEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cv_id", nullable = false)
    @Setter
    private Cv cv;

    @Column(name = "institution", nullable = false)
    @Setter
    private String institution;

    @Column(name = "degree")
    @Setter
    private String degree;

    @Column(name = "field_of_study")
    @Setter
    private String fieldOfStudy;

    @Column(name = "start_date")
    @Setter
    private LocalDate startDate;

    @Column(name = "end_date")
    @Setter
    private LocalDate endDate;

    @Column(name = "description", columnDefinition = "text")
    @Setter
    private String description;

    @Column(name = "display_order", nullable = false)
    @Setter
    private int displayOrder;

    public CvEducationEntry(Cv cv, String institution) {
        this.cv = cv;
        this.institution = institution;
    }
}
