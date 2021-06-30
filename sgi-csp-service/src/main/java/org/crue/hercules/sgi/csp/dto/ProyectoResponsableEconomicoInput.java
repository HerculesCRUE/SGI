package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;

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
public class ProyectoResponsableEconomicoInput implements Serializable {
  private Long id;

  @NotEmpty
  @Size(max = ProyectoResponsableEconomico.PERSONA_REF_LENGTH)
  private String personaRef;

  private Instant fechaInicio;
  private Instant fechaFin;
}
