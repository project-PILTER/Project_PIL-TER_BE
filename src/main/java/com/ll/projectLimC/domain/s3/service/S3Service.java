package com.ll.projectLimC.domain.s3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    // 1. 원본 버킷과 썸네일 버킷을 각각 주입받습니다.
    @Value("${cloud.aws.s3.origin-bucket}")
    private String originBucket;

    @Value("${cloud.aws.s3.thumb-bucket}")
    private String thumbBucket;

    @Value("${cloud.aws.s3.cloudfront-domain}")
    private String cloudFrontDomain;

    // 파일 업로드 메서드
    public String uploadFile(MultipartFile file, String dirName) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        // 전달받은 폴더 이름 뒤에 슬래시를 붙여서 경로 완성
        String fileName = dirName + "/" + UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            // 사용자가 올린 원본 파일은 원본 버킷(origin-bucket)에 저장
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(originBucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // 3. S3 직접 접근 URL 대신 CloudFront 도메인 주소를 조합하여 반환
            // (Lambda가 썸네일 버킷에 동일한 이름으로 파일을 생성하므로 CloudFront를 통해 썸네일에 접근 가능)
            return cloudFrontDomain + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    // 파일 삭제 메서드 (원본과 썸네일 버킷 모두에서 삭제)
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        // URL에서 파일 키(경로 포함 전체 파일명) 추출
        // Ex: https://d299mtvm1wus1q.cloudfront.net/community/uuid_filename.jpg -> community/uuid_filename.jpg 추출
        String fileName;
        try {
            java.net.URI uri = new java.net.URI(fileUrl);
            fileName = uri.getPath().substring(1); // 맨 앞의 '/' 제거
        } catch (Exception e) {
            // 예외 발생 시 기존 방식처럼 파싱
            fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }

        // 원본 버킷과 썸네일 버킷 양쪽에 삭제 요청을 보내 찌꺼기가 남지 X
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(originBucket).key(fileName).build());
        } catch (Exception e) {
            log.warn("원본 S3 파일 삭제 중 예외 발생 (무시됨): {}", e.getMessage());
        }

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(thumbBucket).key(fileName).build());
        } catch (Exception e) {
            log.warn("썸네일 S3 파일 삭제 중 예외 발생 (무시됨): {}", e.getMessage());
        }
    }
}