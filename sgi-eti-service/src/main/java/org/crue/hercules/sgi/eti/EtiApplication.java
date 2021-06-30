package org.crue.hercules.sgi.eti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class EtiApplication {

  public static void main(String[] args) {
    log.debug("main(String[] args) - start");
    SpringApplication.run(EtiApplication.class, args);
    log.debug("main(String[] args) - end");
  }

}
