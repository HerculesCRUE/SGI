package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConvocatoriaBaremacionLogOutput implements Serializable {
  private Long id;
  private Instant creationDate;
  private String trace;
  private Long convocatoriaBaremacionId;
}
