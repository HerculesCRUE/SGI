package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.prc.model.Baremo.TipoCuantia;

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
public class BaremoInput implements Serializable {
  @Max(100)
  @Min(0)
  private Integer peso;

  private BigDecimal puntos;

  private BigDecimal cuantia;

  private TipoCuantia tipoCuantia;

  @NotNull
  private Long configuracionBaremoId;

  private Long convocatoriaBaremacionId;
}
