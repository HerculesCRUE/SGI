package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.prc.model.BaseActivableEntity;

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
public class ConvocatoriaBaremacionInput implements Serializable {
  @NotEmpty
  @Size(max = BaseActivableEntity.NOMBRE_CONVOCATORIA_LENGTH)
  private String nombre;

  @NotNull
  private Integer anio;

  @NotNull
  private Integer aniosBaremables;

  @NotNull
  private Integer ultimoAnio;

  @NotNull
  private BigDecimal importeTotal;

  @Size(max = BaseActivableEntity.PARTIDA_PRESUPUESTARIA_LENGTH)
  private String partidaPresupuestaria;
}
