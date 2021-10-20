package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodoTitularidadOutput implements Serializable {

  private Long id;
  private Long invencionId;
  private Instant fechaInicio;
  private Instant fechaFin;

}
