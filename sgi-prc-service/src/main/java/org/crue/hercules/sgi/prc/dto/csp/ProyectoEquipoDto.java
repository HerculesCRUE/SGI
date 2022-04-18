package org.crue.hercules.sgi.prc.dto.csp;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProyectoEquipoDto implements Serializable {

  private Long id;
  private String personaRef;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Boolean ip;
}
