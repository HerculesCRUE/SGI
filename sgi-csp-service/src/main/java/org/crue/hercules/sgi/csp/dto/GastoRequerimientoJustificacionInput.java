package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
public class GastoRequerimientoJustificacionInput implements Serializable {

  @Size(max = BaseEntity.EXTERNAL_REF_LENGTH)
  private String gastoRef;

  private Long requerimientoJustificacionId;

  private BigDecimal importeAceptado;

  private BigDecimal importeRechazado;

  private BigDecimal importeAlegado;

  private Boolean aceptado;

  private List<I18nFieldValueDto> incidencia;

  private List<I18nFieldValueDto> alegacion;

  @Size(max = BaseEntity.EXTERNAL_REF_LENGTH)
  private String identificadorJustificacion;
}
