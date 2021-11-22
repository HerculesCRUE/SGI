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
public class RepartoIngresoOutput {
  private Long id;
  private Long repartoId;
  private Long invencionIngresoId;
  private BigDecimal importeARepartir;
}
