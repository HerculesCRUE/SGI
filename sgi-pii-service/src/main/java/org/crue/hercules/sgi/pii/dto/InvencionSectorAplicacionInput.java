package org.crue.hercules.sgi.pii.dto;

import javax.validation.constraints.NotNull;

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
public class InvencionSectorAplicacionInput {
  /** Invencion Id */
  @NotNull
  private Long invencionId;

  /** Id del Sector Aplicación. */
  @NotNull
  private Long sectorAplicacionId;
}
