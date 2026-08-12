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
    private final ObjectMapper objectMapper;

    @Value("${public.api.service-key}")
    private String serviceKey;

    @Transactional
    public void fetchAndSaveMedicinesFromPortal() throws Exception {
        // API 상세 경로를 포함한 URL 설정[cite: 1]
        String baseUrl = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("type", "json")
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .build(true)
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        String responseStr = restTemplate.getForObject(uri, String.class);

        // JSON 파싱
        JsonNode root = objectMapper.readTree(responseStr);
        JsonNode items = root.path("body").path("items");

        for (JsonNode item : items) {
            String name = item.path("itemName").asText();

            if (medicineRepository.findByMedicineName(name).isPresent()) continue;

            Medicine medicine = Medicine.builder()
                    .medicineName(name)
                    .manufacturer(item.path("entpName").asText())
                    .efficacy(item.path("efcyQesitm").asText())
                    .dosage(item.path("useMethodQesitm").asText())
                    .precautions(item.path("atpnQesitm").asText())
                    .build();

            medicineRepository.save(medicine);
        }
    }
}
