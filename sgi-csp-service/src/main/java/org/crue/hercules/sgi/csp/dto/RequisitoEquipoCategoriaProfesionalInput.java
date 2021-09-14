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
public class RequisitoEquipoCategoriaProfesionalInput {
  /** RequisitoEquipo Id */
  private Long requisitoEquipoId;

  /** Referencia a la entidad externa CategoriaProfesional del ESB */
  private String categoriaProfesionalRef;
}
