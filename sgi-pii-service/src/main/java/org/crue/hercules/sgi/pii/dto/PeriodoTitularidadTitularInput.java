package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular;

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
public class PeriodoTitularidadTitularInput implements Serializable {

  @NotNull
  @Size(max = PeriodoTitularidadTitular.REF_LENGTH)
  private String titularRef;

  @NotNull
  @Max(PeriodoTitularidadTitular.PARTICIPACION_MAX)
  @Min(PeriodoTitularidadTitular.PARTICIPACION_MIN)
  private BigDecimal participacion;

}
