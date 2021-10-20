package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.pii.model.InvencionIngreso.Estado;

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
public class InvencionIngresoInput implements Serializable {
  @NotNull
  private Long invencionId;

  @NotEmpty
  private String ingresoRef;

  @Min(0)
  private BigDecimal importePendienteRepartir;

  @NotNull
  private Estado estado;
}
