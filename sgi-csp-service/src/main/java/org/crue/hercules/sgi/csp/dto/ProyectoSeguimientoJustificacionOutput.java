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
public class ProyectoSeguimientoJustificacionOutput implements Serializable {
  private Long id;
  private ProyectoProyectoSgeOutput proyectoProyectoSge;
  private BigDecimal importeJustificado;
  private BigDecimal importeJustificadoCD;
  private BigDecimal importeJustificadoCI;
  private BigDecimal importeAceptado;
  private BigDecimal importeAceptadoCD;
  private BigDecimal importeAceptadoCI;
  private BigDecimal importeRechazado;
  private BigDecimal importeRechazadoCD;
  private BigDecimal importeRechazadoCI;
  private BigDecimal importeAlegado;
  private BigDecimal importeAlegadoCD;
  private BigDecimal importeAlegadoCI;
  private BigDecimal importeReintegrar;
  private BigDecimal importeReintegrarCD;
  private BigDecimal importeReintegrarCI;
  private BigDecimal importeReintegrado;
  private BigDecimal importeReintegradoCD;
  private BigDecimal importeReintegradoCI;
  private BigDecimal interesesReintegrados;
  private BigDecimal interesesReintegrar;
  private Instant fechaReintegro;
  private String justificanteReintegro;
  private BigDecimal importeNoEjecutado;
  private BigDecimal importeNoEjecutadoCD;
  private BigDecimal importeNoEjecutadoCI;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProyectoProyectoSgeOutput implements Serializable {
    private Long id;
    private Long proyectoId;
    private String proyectoSgeRef;
  }
}
