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
public class ContextoProyectoDto extends BaseRestDto {
  /** Propiedad resultados */
  public enum PropiedadResultados {
    /** Sin resultados */
    SIN_RESULTADOS,
    /** Universidad */
    UNIVERSIDAD,
    /** Entidad financiadora */
    ENTIDAD_FINANCIADORA,
    /** Compartida */
    COMPARTIDA;
  }

  private Long proyectoId;
  private String objetivos;
  private String intereses;
  private String resultadosPrevistos;
  private PropiedadResultados propiedadResultados;
  private AreaTematicaDto areaTematicaConvocatoria;
  private AreaTematicaDto areaTematica;
  private ProyectoDto proyecto;
}