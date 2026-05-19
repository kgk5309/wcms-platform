package com.wcms.auth.infra.config;

import com.wcms.auth.application.AuthCleanupProperties;
import com.wcms.auth.application.AuthLoginProperties;
import com.wcms.auth.application.AuthTokenProperties;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({AuthTokenProperties.class, AuthLoginProperties.class, AuthCleanupProperties.class})
public class AuthConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
