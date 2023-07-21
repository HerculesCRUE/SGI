package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ComiteDto extends BaseRestDto {

  private String comite;
  private String nombreInvestigacion;
  private Genero genero;
  private FormularioDto formulario;
  private Boolean activo;

  /** Género de nombre de investigación */
  public enum Genero {
    /** Femenino */
    F,
    /** Masculino */
    M;
  }
}