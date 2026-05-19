package com.wcms.auth.infra.config;

import com.wcms.auth.application.AuthLoginProperties;
import com.wcms.auth.application.AuthTokenProperties;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuthTokenProperties.class, AuthLoginProperties.class})
public class AuthConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
