package com.roles.usermanagement.web.config;

import com.roles.usermanagement.domain.service.SecurityBootstrapService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityBootstrapConfig {
    @Bean
    public ApplicationRunner initializeSecurity(SecurityBootstrapService bootstrap) {
        return args -> bootstrap.initialize();
    }
}