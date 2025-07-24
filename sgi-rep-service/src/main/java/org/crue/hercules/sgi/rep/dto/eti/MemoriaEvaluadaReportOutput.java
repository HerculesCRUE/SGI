package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class MemoriaEvaluadaReportOutput {
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
