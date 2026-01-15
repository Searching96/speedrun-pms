package org.f3.postalmanagement.config;

import org.f3.postalmanagement.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> {
            try {
                return Optional.ofNullable(SecurityUtils.getCurrentAccount().getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }
}
