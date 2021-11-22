package org.crue.hercules.sgi.tp.scheduling.config;

import java.time.Instant;

import org.springframework.scheduling.config.Task;
import org.springframework.util.Assert;

public class InstantTask extends Task {
  private final Instant instant;

  public InstantTask(Runnable runnable, Instant instant) {
    super(runnable);
    Assert.notNull(instant, "Instant cannot be null");
    this.instant = instant;
  }

  /**
   * Return the associated instant.
   * 
   * @return the associated instant
   */
  public Instant getInstant() {
    return this.instant;
  }
}
