package com.ll.projectLimC.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.OffsetDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "utcDateTimeProvider") // dateTimeProviderRef 지정
public class JpaConfig {

    @Bean
    public DateTimeProvider utcDateTimeProvider() {
        // Auditing 실행 시 OffsetDateTime.now()를 반환하도록 지정
        return () -> Optional.of(OffsetDateTime.now());
    }
}
