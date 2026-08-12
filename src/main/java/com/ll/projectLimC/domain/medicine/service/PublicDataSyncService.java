package com.ll.projectLimC.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.projectLimC.domain.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class PublicDataSyncService {
    private final MedicineRepository medicineRepository;
    private final ObjectMapper objectMapper; // JSON 파싱을 위해 주입 받습니다.

    @Value("${public.api.service-key}")
    private String serviceKey;

    @Transactional
    public void fetchAndSaveMedicinesFromPortal() {
        // 1. 공공데이터 'e약은요' API 상세 주소 설정 (/getDrbEasyDrugList 경로 추가)
        String baseUrl = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";

        // 2. 인증키 및 파라미터 조합
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("type", "json")
                .queryParam("numOfRows", 10) // 테스트로 우선 10개만 가져오기
                .queryParam("pageNo", 1)
                .build(true)
                .toUri();

        // 3. RestTemplate을 이용해 공공데이터 서버로 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        try {
            String responseStr = restTemplate.getForObject(uri, String.class);

            // 4. 받아온 JSON 응답 파싱 및 DB 저장
            JsonNode root = objectMapper.readTree(responseStr);
            JsonNode items = root.path("body").path("items");

            for (JsonNode item : items) {
                String name = item.path("itemName").asText();

                // 이미 DB에 존재하는 약 이름이라면 중복 저장을 막기 위해 건너뜁니다.
                if (medicineRepository.findByName(name).isPresent()) {
                    continue;
                }

                // 식약처 API 필드명에 맞춰 엔티티 빌더 생성
                Medicine medicine = Medicine.builder()
                        .medicineName(name)
                        .manufacturer(item.path("entpName").asText())         // 제조사
                        .efficacy(item.path("efcyQesitm").asText())           // 효능·효과
                        .dosage(item.path("useMethodQesitm").asText())        // 용법·용량
                        .precautions(item.path("atpnQesitm").asText())        // 주의사항
                        .build();

                // DB에 저장
                medicineRepository.save(medicine);
            }

            System.out.println("공공데이터 동기화 및 DB 저장 성공!");

        } catch (Exception e) {
            System.err.println("공공데이터 호출 중 에러 발생: " + e.getMessage());
        }
    }
}
