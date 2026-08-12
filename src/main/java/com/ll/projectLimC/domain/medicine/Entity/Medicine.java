package com.ll.projectLimC.domain.medicine.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제품명
    private String medicineName;

    // 제조사명
    private String manufacturer;

    @Column(columnDefinition = "TEXT")
    private String efficacy;       // 효능

    @Column(columnDefinition = "TEXT")
    private String dosage;         // 용법

    @Column(columnDefinition = "TEXT")
    private String precautions;    // 주의사항
}
