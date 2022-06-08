package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico;

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
public class GrupoResponsableEconomicoInput implements Serializable {
  private Long id;

  @Size(max = GrupoResponsableEconomico.PERSONA_REF_LENGTH)
  @NotBlank
  private String personaRef;

  private Instant fechaInicio;

  private Instant fechaFin;

  @NotNull
  private Long grupoId;

}
