package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

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
public class AlegacionRequerimientoOutput implements Serializable {
  private Long id;
  private Long requerimientoJustificacionId;
  private Instant fechaAlegacion;
  private BigDecimal importeAlegado;
  private BigDecimal importeAlegadoCd;
  private BigDecimal importeAlegadoCi;
  private BigDecimal importeReintegrado;
  private BigDecimal importeReintegradoCd;
  private BigDecimal importeReintegradoCi;
  private BigDecimal interesesReintegrados;
  private Instant fechaReintegro;
  private String justificanteReintegro;
  private String observaciones;
}
