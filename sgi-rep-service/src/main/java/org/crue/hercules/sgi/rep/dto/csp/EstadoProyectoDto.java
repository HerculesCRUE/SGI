package org.crue.hercules.sgi.rep.dto.csp;

import java.time.Instant;

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
public class EstadoProyectoDto extends BaseRestDto {

  /**
   * Estados del proyecto
   */
  public enum TipoEstadoProyecto {
    /** Borrador */
    BORRADOR,
    /** Concedido */
    CONCEDIDO,
    /** Renunciado */
    RENUNCIADO,
    /** Rescindido */
    RESCINDIDO;
  }

  private Long id;
  private Long proyectoId;
  private TipoEstadoProyecto estado;
  private Instant fechaEstado;
  private String comentario;
  private ProyectoDto proyecto;
}