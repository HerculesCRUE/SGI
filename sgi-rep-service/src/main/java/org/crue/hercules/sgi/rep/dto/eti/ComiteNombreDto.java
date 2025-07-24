package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComiteNombreDto extends I18nFieldValueDto {
  /** Género de nombre de investigación */
  public enum Genero {
    /** Femenino */
    F,
    /** Masculino */
    M;
  }

  private Genero genero;
}