package org.crue.hercules.sgi.usr.config;

import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiJpaRepository;
import org.crue.hercules.sgi.framework.web.config.SgiDataConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * DataConfig
 * 
 * Jpa configuration.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "org.crue.hercules.sgi.usr.repository" }, repositoryBaseClass = SgiJpaRepository.class)
public class DataConfig extends SgiDataConfig {
}