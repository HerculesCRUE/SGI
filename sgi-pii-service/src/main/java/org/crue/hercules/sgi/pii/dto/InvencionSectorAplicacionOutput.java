package org.crue.hercules.sgi.pii.dto;

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
public class InvencionSectorAplicacionOutput {
  /** Id */
  public Long id;

  /** Invencion Id */
  private Long invencionId;

  /** Sector Aplicación. */
  private SectorAplicacionOutput sectorAplicacion;
}
