package org.crue.hercules.sgi.prc.config;

import org.crue.hercules.sgi.framework.problem.spring.boot.autoconfigure.EnableProblem;
import org.crue.hercules.sgi.framework.spring.context.support.boot.autoconfigure.EnableApplicationContextSupport;
import org.crue.hercules.sgi.framework.web.config.SgiWebConfig;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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

  /**
   * Returns a new {@link ModelMapper}.
   * 
   * @return the {@link ModelMapper}
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    return modelMapper;
  }

}