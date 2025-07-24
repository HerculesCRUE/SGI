package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

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
  private List<I18nFieldValueDto> observaciones;
}
