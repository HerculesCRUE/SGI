package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.csp.model.Programa;

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
public class ProyectoEntidadConvocanteDto implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  private Long id;

  /** Entidad Convocante */
  private String entidadRef;

  /** Programa heredado de la Convocatoria */
  private Programa programaConvocatoria;

  /** Programa */
  private Programa programa;

}
