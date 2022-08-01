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
public class RequerimientoJustificacionOutput implements Serializable {
  private Long id;
  private Long proyectoProyectoSgeId;
  private Integer numRequerimiento;
  private TipoRequerimiento tipoRequerimiento;
  private Long proyectoPeriodoJustificacionId;
  private Long requerimientoPrevioId;
  private Instant fechaNotificacion;
  private Instant fechaFinAlegacion;
  private String observaciones;
  private BigDecimal importeAceptadoCd;
  private BigDecimal importeAceptadoCi;
  private BigDecimal importeRechazadoCd;
  private BigDecimal importeRechazadoCi;
  private BigDecimal importeReintegrar;
  private BigDecimal importeReintegrarCd;
  private BigDecimal importeReintegrarCi;
  private BigDecimal interesesReintegrar;
  private BigDecimal importeAceptado;
  private BigDecimal importeRechazado;
  private BigDecimal subvencionJustificada;
  private BigDecimal defectoSubvencion;
  private BigDecimal anticipoJustificado;
  private BigDecimal defectoAnticipo;
  private Boolean recursoEstimado;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoRequerimiento implements Serializable {
    private Long id;
    private String nombre;
  }
}
