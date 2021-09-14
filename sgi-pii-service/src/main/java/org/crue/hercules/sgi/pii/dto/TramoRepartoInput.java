package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class TramoRepartoInput implements Serializable {
  @NotNull
  @Min(0)
  private Integer desde;

  @NotNull
  @Min(0)
  private Integer hasta;

  @NotNull
  @Min(0)
  @Max(100)
  private BigDecimal porcentajeUniversidad;

  @NotNull
  @Min(0)
  @Max(100)
  private BigDecimal porcentajeInventores;
}
