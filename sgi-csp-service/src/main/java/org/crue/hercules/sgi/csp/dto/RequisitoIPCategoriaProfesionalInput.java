package org.crue.hercules.sgi.csp.dto;

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
public class RequisitoIPCategoriaProfesionalInput {
  /** RequisitoIP Id */
  private Long requisitoIPId;

  /** Referencia a la entidad externa CategoriaProfesional del ESB */
  private String categoriaProfesionalRef;
}
