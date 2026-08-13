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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicDataSyncService {
    private final MedicineRepository medicineRepository;
    private final ObjectMapper objectMapper;

    @Value("${public.api.service-key}")
    private String serviceKey;

    @Scheduled(cron = "0 0 3 * * MON")
    @Transactional
    public void scheduledSync() {
        try {
            System.out.println("=== 공공데이터 자동 동기화 시작 ===");
            fetchAndSaveMedicinesFromPortal();
            System.out.println("=== 공공데이터 자동 동기화 완료 ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void fetchAndSaveMedicinesFromPortal() throws Exception {
        String baseUrl = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";

        int pageNo = 1;
        int numOfRows = 100;
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

            JsonNode root = objectMapper.readTree(responseStr);
            JsonNode items = root.path("body").path("items");

            if (items.isMissingNode() || items.isEmpty() || !items.isArray()) {
                break;
            }

            for (JsonNode item : items) {
                String itemSeq = item.path("itemSeq").asText();
                String name = item.path("itemName").asText();
                String manufacturer = item.path("entpName").asText();
                String efficacy = item.path("efcyQesitm").asText();
                String useMethod = item.path("useMethodQesitm").asText();
                String atpn = item.path("atpnQesitm").asText();
                String atpnWarn = item.path("atpnWarnQesitm").asText();
                String itemImage = item.path("itemImage").asText();
                String depositMethod = item.path("depositMethodQesitm").asText();

                // 1. 이미 존재하는 약 이름인지 확인
                Optional<Medicine> existingMedicine = medicineRepository.findFirstByMedicineName(name);

                if (existingMedicine.isPresent()) {
                    // 2-A. 이미 존재한다면 최신 데이터로 값 변경 (JPA 더티 체킹에 의해 자동 Update 됨)
                    Medicine medicine = existingMedicine.get();
                    medicine.updateInfo(itemSeq, manufacturer, efficacy, useMethod, atpn, atpnWarn, itemImage, depositMethod);
                } else {
                    // 2-B. 존재하지 않는다면 새로 생성해서 저장
                    Medicine medicine = Medicine.builder()
                            .itemSeq(itemSeq)
                            .medicineName(name)
                            .manufacturer(manufacturer)
                            .efficacy(efficacy)
                            .useMethodQesitm(useMethod)
                            .atpnQesitm(atpn)
                            .atpnWarnQesitm(atpnWarn)
                            .itemImage(itemImage)
                            .depositMethodQesitm(depositMethod)
                            .build();

                    medicineRepository.save(medicine);
                }
            }

            if (items.size() < numOfRows) {
                break;
            }

            pageNo++;
        }
    }
}
