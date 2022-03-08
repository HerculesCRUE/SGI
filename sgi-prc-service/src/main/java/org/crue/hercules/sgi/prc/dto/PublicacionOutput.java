package org.crue.hercules.sgi.prc.dto;

import java.time.Instant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PublicacionOutput extends ProduccionCientificaOutput {
  private String tituloPublicacion;
  private String tipoProduccion;
  private Instant fechaPublicacion;
}
