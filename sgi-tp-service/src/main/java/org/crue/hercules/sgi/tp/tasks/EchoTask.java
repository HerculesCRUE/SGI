package org.crue.hercules.sgi.tp.tasks;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EchoTask {

  public void echo(String message) {
    log.info(message);
  }
}
