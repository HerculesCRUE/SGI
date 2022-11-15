package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo.Dedicacion;

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
public class GrupoEquipoUpdateInput implements Serializable {
  private Long id;

  @Size(max = GrupoEquipo.PERSONA_REF_LENGTH)
  @NotBlank
  private String personaRef;

  @NotNull
  private Instant fechaInicio;

  private Instant fechaFin;

  @NotNull
  private Long rolId;

  @NotNull
  private Dedicacion dedicacion;

  @Min(GrupoEquipo.PARTICIPACION_MIN)
  @Max(GrupoEquipo.PARTICIPACION_MAX)
  @NotNull
  private BigDecimal participacion;

  @NotNull
  private Long grupoId;

}
