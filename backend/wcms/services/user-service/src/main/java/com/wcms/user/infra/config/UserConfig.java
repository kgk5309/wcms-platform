package com.wcms.user.infra.config;

import com.wcms.user.application.UserTokenProperties;
import com.wcms.user.application.AuthServiceProperties;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({UserTokenProperties.class, AuthServiceProperties.class})
public class UserConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
