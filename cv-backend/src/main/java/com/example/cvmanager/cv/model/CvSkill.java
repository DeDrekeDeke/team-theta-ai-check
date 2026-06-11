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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cv_skill")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CvSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cv_id", nullable = false)
    @Setter
    private Cv cv;

    @Column(name = "name", nullable = false)
    @Setter
    private String name;

    @Column(name = "category")
    @Setter
    private String category;

    @Column(name = "proficiency")
    @Setter
    private String proficiency;

    @Column(name = "display_order", nullable = false)
    @Setter
    private int displayOrder;

    public CvSkill(Cv cv, String name) {
        this.cv = cv;
        this.name = name;
    }
}
