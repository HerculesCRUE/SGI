package org.crue.hercules.sgi.rep.dto.csp;

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
public class RolProyectoDto extends BaseRestDto {

  /**
   * Equipos.
   *
   */
  public enum Equipo {

    /** Equipo de investigación */
    INVESTIGACION,
    /** Equipo de trabajo */
    TRABAJO;
  }

  /**
   * Tipos de orden.
   *
   */
  public enum Orden {

    /** Orden primario */
    PRIMARIO,
    /** Orden Secundario */
    SECUNDARIO;
  }

  private Long id;
  private String abreviatura;
  private String nombre;
  private String descripcion;
  private Boolean rolPrincipal;
  private Orden orden;
  private Equipo equipo;
  private Boolean activo;
}