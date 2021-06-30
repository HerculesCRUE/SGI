package org.crue.hercules.sgi.pii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class PiiApplication {

  public static void main(String[] args) {
    log.debug("main(String[] args) - start");
    SpringApplication.run(PiiApplication.class, args);
    log.debug("main(String[] args) - end");
  }

}
