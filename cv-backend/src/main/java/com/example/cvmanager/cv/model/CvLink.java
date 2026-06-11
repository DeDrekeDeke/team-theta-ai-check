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
@Table(name = "cv_link")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CvLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cv_id", nullable = false)
    @Setter
    private Cv cv;

    @Column(name = "label", nullable = false)
    @Setter
    private String label;

    @Column(name = "url", nullable = false, length = 1000)
    @Setter
    private String url;

    @Column(name = "display_order", nullable = false)
    @Setter
    private int displayOrder;

    public CvLink(Cv cv, String label, String url) {
        this.cv = cv;
        this.label = label;
        this.url = url;
    }
}
