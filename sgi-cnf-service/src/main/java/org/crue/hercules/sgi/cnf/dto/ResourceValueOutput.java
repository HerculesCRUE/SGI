package org.crue.hercules.sgi.cnf.dto;

import java.time.Instant;

/**
 * This DTO represent the resource value
 */
public interface ResourceValueOutput {
  byte[] getValue();

  Instant getLastModifiedDate();

}