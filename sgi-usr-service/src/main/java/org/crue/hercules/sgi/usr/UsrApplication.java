package org.crue.hercules.sgi.usr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class UsrApplication {

  public static void main(String[] args) {
    log.debug("main(String[] args) - start");
    SpringApplication.run(UsrApplication.class, args);
    log.debug("main(String[] args) - end");
  }

}
