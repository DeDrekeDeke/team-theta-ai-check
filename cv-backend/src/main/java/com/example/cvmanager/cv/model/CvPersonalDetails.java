package com.example.cvmanager.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cv_personal_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CvPersonalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cv_id", nullable = false, unique = true)
    @Setter
    private Cv cv;

    @Column(name = "full_name")
    @Setter
    private String fullName;

    @Column(name = "email")
    @Setter
    private String email;

    @Column(name = "phone")
    @Setter
    private String phone;

    @Column(name = "location")
    @Setter
    private String location;

    @Column(name = "headline")
    @Setter
    private String headline;

    public CvPersonalDetails(Cv cv) {
        this.cv = cv;
    }
}
