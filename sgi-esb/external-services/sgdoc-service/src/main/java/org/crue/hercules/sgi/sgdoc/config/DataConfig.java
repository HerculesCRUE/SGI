package org.crue.hercules.sgi.sgdoc.config;

import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiJpaRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * DataConfig
 * 
 * Jpa configuration.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "org.crue.hercules.sgi.sgdoc.repository" }, repositoryBaseClass = SgiJpaRepository.class)
public class DataConfig {

}