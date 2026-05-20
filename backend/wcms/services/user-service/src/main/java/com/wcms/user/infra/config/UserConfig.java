package com.wcms.user.infra.config;

import com.wcms.user.application.UserTokenProperties;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UserTokenProperties.class)
public class UserConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
