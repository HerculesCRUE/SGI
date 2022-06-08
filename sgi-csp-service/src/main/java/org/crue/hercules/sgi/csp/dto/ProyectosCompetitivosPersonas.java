package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

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
public class ProyectosCompetitivosPersonas implements Serializable {
  private Long numProyectosCompetitivos;
  private Long numProyectosCompetitivosActuales;
  private Long numProyectosNoCompetitivos;
  private Long numProyectosNoCompetitivosActuales;
}
