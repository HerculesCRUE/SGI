package org.crue.hercules.sgi.pii.dto;

import java.math.BigDecimal;

import org.crue.hercules.sgi.pii.model.InvencionIngreso.Estado;

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
public class InvencionIngresoOutput {
  private Long id;
  private Long invencionId;
  private String ingresoRef;
  private BigDecimal importePendienteRepartir;
  private Estado estado;
}
