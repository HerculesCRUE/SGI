package org.crue.hercules.sgi.sgdoc.config;

import org.crue.hercules.sgi.framework.problem.spring.boot.autoconfigure.EnableProblem;
import org.crue.hercules.sgi.framework.spring.context.support.boot.autoconfigure.EnableApplicationContextSupport;
import org.crue.hercules.sgi.framework.web.config.SgiWebConfig;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SgiWebConfig Fw
 * 
 * Framework Web configuration.
 */
@Configuration
@EnableProblem
@EnableApplicationContextSupport
public class WebConfig extends SgiWebConfig {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}
