package com.ll.projectLimC.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.projectLimC.domain.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class PublicDataSyncService {
    private final MedicineRepository medicineRepository;
    private final ObjectMapper objectMapper;

    @Value("${public.api.service-key}")
    private String serviceKey;

    @Scheduled(cron = "0 0 3 * * MON")// 매주 월요일 새벽 3시 실행
    @Transactional
    public void scheduledSync() {
        try {
            System.out.println("=== 공공데이터 자동 동기화 시작 ===");
            fetchAndSaveMedicinesFromPortal();
            System.out.println("=== 공공데이터 자동 동기화 완료 ===");
        } catch (Exception e) {
            e.printStackTrace();
            // 실무에서는 로그 처리(log.error)를 사용합니다.
        }
    }

    @Transactional
    public void fetchAndSaveMedicinesFromPortal() throws Exception {
        // API 상세 경로를 포함한 URL 설정
        String baseUrl = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";

        int pageNo = 1;
        int numOfRows = 100; // 한 번에 가져올 데이터 개수 설정
        RestTemplate restTemplate = new RestTemplate();

        while (true) {
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("type", "json")
                    .queryParam("numOfRows", numOfRows)
                    .queryParam("pageNo", pageNo)
                    .build(true)
                    .toUri();

            String responseStr = restTemplate.getForObject(uri, String.class);

            // JSON 파싱
            JsonNode root = objectMapper.readTree(responseStr);
            JsonNode items = root.path("body").path("items");

            // 더 이상 가져올 데이터가 없거나 비어있으면 반복문 종료
            if (items.isMissingNode() || items.isEmpty() || !items.isArray()) {
                break;
            }

            for (JsonNode item : items) {
                String name = item.path("itemName").asText();

                if (medicineRepository.existsByMedicineName(name)) continue;

                Medicine medicine = Medicine.builder()
                        .medicineName(name)
                        .manufacturer(item.path("entpName").asText())
                        .efficacy(item.path("efcyQesitm").asText())
                        .dosage(item.path("useMethodQesitm").asText())
                        .precautions(item.path("atpnQesitm").asText())
                        .build();

                medicineRepository.save(medicine);
            }

            // 마지막 페이지 도달 확인 (가져온 데이터 개수가 요청한 수보다 적으면 끝)
            if (items.size() < numOfRows) {
                break;
            }

            pageNo++; // 다음 페이지 번호로 증가
        }
    }
}
