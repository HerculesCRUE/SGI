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
public class ProyectoPeriodoJustificacionSeguimientoInput implements Serializable {

  @NotNull
  private Long proyectoPeriodoJustificacionId;
  @NotNull
  private Long proyectoAnualidadId;
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
  @Size(max = BaseEntity.DEFAULT_TEXT_LENGTH)
  private String justificanteReintegro;
  private BigDecimal importeNoEjecutado;
  private BigDecimal importeNoEjecutadoCD;
  private BigDecimal importeNoEjecutadoCI;
}
