package org.crue.hercules.sgi.csp.dto;

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
public class FacturaEmitidaOutput {

  private String comentario;
  private Instant fechaEmision;
  private BigDecimal importeBase;
  private Integer numeroPrevision;
  private Long proyectoIdSGI;
}
