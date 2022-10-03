package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.BaseEntity;

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
public class RequerimientoJustificacionInput implements Serializable {
  @NotNull
  private Long proyectoProyectoSgeId;
  @NotNull
  private Long tipoRequerimientoId;
  private Long proyectoPeriodoJustificacionId;
  private Long requerimientoPrevioId;
  private Instant fechaNotificacion;
  private Instant fechaFinAlegacion;
  @Size(max = BaseEntity.DEFAULT_LONG_TEXT_LENGTH)
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
}
