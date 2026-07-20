package com.ll.projectLimC.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "utcDateTimeProvider") // dateTimeProviderRef 지정
public class JpaConfig {

    @Bean
    public DateTimeProvider utcDateTimeProvider() {
        // ZoneOffset.ofHours(9)를 사용해 오프셋(+09:00)을 고정한 OffsetDateTime 전달
        return () -> Optional.of(OffsetDateTime.now(ZoneOffset.ofHours(9)));
    }
}
