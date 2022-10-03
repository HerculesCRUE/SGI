package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
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
public class GastoRequerimientoJustificacionOutput implements Serializable {
  private Long id;
  private String gastoRef;
  private Long requerimientoJustificacionId;
  private BigDecimal importeAceptado;
  private BigDecimal importeRechazado;
  private BigDecimal importeAlegado;
  private Boolean aceptado;
  private String incidencia;
  private String alegacion;
  private String identificadorJustificacion;
}
