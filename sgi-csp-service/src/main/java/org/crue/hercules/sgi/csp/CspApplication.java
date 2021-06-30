package org.crue.hercules.sgi.csp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ConfigurationPropertiesScan("org.crue.hercules.sgi.csp.config")
@Slf4j
public class CspApplication {

  public static void main(String[] args) {
    log.debug("main(String[] args) - start");
    SpringApplication.run(CspApplication.class, args);
    log.debug("main(String[] args) - end");
  }

}
