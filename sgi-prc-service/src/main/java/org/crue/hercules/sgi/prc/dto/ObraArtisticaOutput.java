package org.crue.hercules.sgi.prc.dto;

import java.time.Instant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ObraArtisticaOutput extends ProduccionCientificaOutput {
  private Instant fechaInicio;
  private String descripcion;
}
