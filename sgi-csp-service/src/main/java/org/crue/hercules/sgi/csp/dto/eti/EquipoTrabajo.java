package org.crue.hercules.sgi.csp.dto.eti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*EquipoTrabajo del API del módulo de Etica*/
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoTrabajo {

  /** Id */
  private Long id;

  /** Referencia usuario */
  private String personaRef;

  /** Peticion Evaluacion */
  private PeticionEvaluacion peticionEvaluacion;

}
