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
public class RequisitoIPNivelAcademicoOutput {
  /** Id */
  private Long id;

  /** RequisitoIP Id */
  private Long requisitoIPId;

  /** Referencia a la entidad externa NivelAcademico del ESB */
  private String nivelAcademicoRef;
}
