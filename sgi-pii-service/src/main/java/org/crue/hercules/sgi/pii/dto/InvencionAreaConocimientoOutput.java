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
public class InvencionAreaConocimientoOutput {
  /** Id */
  public Long id;

  /** Invencion Id */
  private Long invencionId;

  /** Identificador de un Area de Conocimiento */
  private String areaConocimientoRef;
}
