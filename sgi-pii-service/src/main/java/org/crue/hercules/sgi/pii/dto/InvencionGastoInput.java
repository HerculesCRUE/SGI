package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.pii.model.InvencionGasto.Estado;

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
public class InvencionGastoInput implements Serializable {
  @NotNull
  private Long invencionId;

  private Long solicitudProteccionId;

  @NotEmpty
  private String gastoRef;

  @NotNull
  @Min(0)
  private BigDecimal importePendienteDeducir;

  @NotNull
  private Estado estado;
}
