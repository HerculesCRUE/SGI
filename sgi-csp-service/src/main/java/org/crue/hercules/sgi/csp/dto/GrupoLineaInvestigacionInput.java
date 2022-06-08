package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotNull;

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
public class GrupoLineaInvestigacionInput implements Serializable {

  @NotNull
  private Long lineaInvestigacionId;

  @NotNull
  private Instant fechaInicio;

  private Instant fechaFin;

  @NotNull
  private Long grupoId;

}
