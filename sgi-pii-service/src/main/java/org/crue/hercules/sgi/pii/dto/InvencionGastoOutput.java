package org.crue.hercules.sgi.pii.dto;

import java.math.BigDecimal;

import org.crue.hercules.sgi.pii.model.InvencionGasto.Estado;

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
public class InvencionGastoOutput {
  private Long id;
  private Long invencionId;
  private Long solicitudProteccionId;
  private String gastoRef;
  private BigDecimal importePendienteDeducir;
  private Estado estado;
}
