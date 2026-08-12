package com.ll.projectLimC;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.util.TimeZone;

// @EnableJpaAuditing // created_at, updated_at 자동 업데이트
@SpringBootApplication
@EnableScheduling
public class ProjectLimCApplication {

	@PostConstruct
	public void setTimeZone() {
		// 애플리케이션 실행 시 타임존을 한국 시간(KST)으로 고정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		// 소셜 로그인 기능을 구현하고 나니 에러가 나타남.
		// 디렉토리가 존재하지 않으면 실행 전에 자동으로 생성하도록 보완
		File tempDir = new File("./.temp");
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		// 스프링이 실행되기 전에 임시 디렉토리 경로를 프로젝트 하위 폴더(.temp)로 고정
		// 이 코드가 실행되면 윈도우 AppData 권한 문제를 우회함.
//		System.setProperty("java.io.tmpdir", "./.temp");

		SpringApplication.run(ProjectLimCApplication.class, args);
	}

}
