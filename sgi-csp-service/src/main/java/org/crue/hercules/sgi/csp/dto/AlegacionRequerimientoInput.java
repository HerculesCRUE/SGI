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
public class AlegacionRequerimientoInput implements Serializable {
  @NotNull
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
  @Size(max = BaseEntity.DEFAULT_TEXT_LENGTH)
  private String justificanteReintegro;
  @Size(max = BaseEntity.DEFAULT_LONG_TEXT_LENGTH)
  private String observaciones;
}
