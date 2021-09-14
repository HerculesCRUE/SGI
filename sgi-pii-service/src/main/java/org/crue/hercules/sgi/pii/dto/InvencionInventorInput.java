package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.InvencionInventor;

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
public class InvencionInventorInput implements Serializable {

  private Long id;

  @NotNull
  private Long invencionId;

  @NotNull
  @Size(max = InvencionInventor.REF_LENGTH)
  private String inventorRef;

  @NotNull
  @Max(InvencionInventor.PARTICIPACION_MAX)
  @Min(InvencionInventor.PARTICIPACION_MIN)
  private BigDecimal participacion;

  @NotNull
  private Boolean repartoUniversidad;

  @NotNull
  private Boolean activo;

}
