package org.crue.hercules.sgi.pii.dto;

import java.math.BigDecimal;

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
public class RepartoIngresoInput {
  @NotNull
  private Long repartoId;

  @NotNull
  private Long invencionIngresoId;

  @Min(0)
  @NotNull
  private BigDecimal importeARepartir;
}
