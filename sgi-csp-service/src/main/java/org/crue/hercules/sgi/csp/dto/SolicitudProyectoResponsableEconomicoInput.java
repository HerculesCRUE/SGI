package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;

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
public class SolicitudProyectoResponsableEconomicoInput implements Serializable {
  private Long id;

  @NotEmpty
  @Size(max = SolicitudProyectoResponsableEconomico.PERSONA_REF_LENGTH)
  private String personaRef;

  private Integer mesInicio;
  private Integer mesFin;
}
