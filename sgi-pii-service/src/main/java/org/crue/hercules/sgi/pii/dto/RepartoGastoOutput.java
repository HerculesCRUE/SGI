package org.crue.hercules.sgi.pii.dto;

import java.math.BigDecimal;

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
public class RepartoGastoOutput {
  private Long id;
  private Long repartoId;
  private Long invencionGastoId;
  private BigDecimal importeADeducir;
}
