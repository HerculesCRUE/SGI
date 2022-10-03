package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class GastoRequerimientoJustificacionInput implements Serializable {

  @Size(max = BaseEntity.EXTERNAL_REF_LENGTH)
  private String gastoRef;

  private Long requerimientoJustificacionId;

  private BigDecimal importeAceptado;

  private BigDecimal importeRechazado;

  private BigDecimal importeAlegado;

  private Boolean aceptado;

  @Size(max = BaseEntity.DEFAULT_LONG_TEXT_LENGTH)
  private String incidencia;

  @Size(max = BaseEntity.DEFAULT_LONG_TEXT_LENGTH)
  private String alegacion;

  @Size(max = BaseEntity.EXTERNAL_REF_LENGTH)
  private String identificadorJustificacion;
}
