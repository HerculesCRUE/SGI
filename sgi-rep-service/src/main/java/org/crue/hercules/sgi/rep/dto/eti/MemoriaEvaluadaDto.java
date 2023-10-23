package org.crue.hercules.sgi.rep.dto.eti;

import java.io.Serializable;

import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MemoriaEvaluadaDto implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long id;
  private Long evaluacionId;
  private String numReferencia;
  private String personaRef;
  private String dictamen;
  private Integer version;
  private String tipoEvaluacion;
  private String titulo;
  private PersonaDto responsable;
}
