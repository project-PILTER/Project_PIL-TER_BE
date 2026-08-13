package com.ll.projectLimC.domain.medicine.entity;

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
    private Long id; // 약품 id

    @Column(columnDefinition = "TEXT")
    private String medicineName;// 제품명

    @Column(columnDefinition = "TEXT")
    private String manufacturer;// 제조사명

    @Column(columnDefinition = "TEXT")
    private String efficacy;// 효능

    @Column(columnDefinition = "TEXT")
    private String useMethodQesitm;// 복용 용법

    @Column(columnDefinition = "TEXT")
    private String atpnQesitm;// 주의사항

    @Column(columnDefinition = "TEXT")
    private String atpnWarnQesitm; // 부작용

    @Column(columnDefinition = "TEXT")
    private String itemImage; // 약 이미지

    @Column(columnDefinition = "TEXT")
    private String depositMethodQesitm; // 보관방법
}
