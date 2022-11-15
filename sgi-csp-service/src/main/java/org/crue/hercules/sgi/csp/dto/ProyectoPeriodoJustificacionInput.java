package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.csp.enums.TipoJustificacion;

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
public class ProyectoPeriodoJustificacionInput implements Serializable {

  private Long id;
  private Long proyectoId;
  private Long numPeriodo;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Instant fechaInicioPresentacion;
  private Instant fechaFinPresentacion;
  private TipoJustificacion tipoJustificacion;
  private String observaciones;
  private Long convocatoriaPeriodoJustificacionId;
  private EstadoProyectoPeriodoJustificacion estado;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EstadoProyectoPeriodoJustificacion implements Serializable {

    /** Enumerado Tipo de Seguimiento */
    public enum TipoEstadoPeriodoJustificacion {
      /** Pendiente */
      PENDIENTE,
      /** Elaboracion */
      ELABORACION,
      /** Entregada */
      ENTREGADA,
      /** Subsanación */
      SUBSANACION,
      /** Cerrada */
      CERRADA;
    }

    /**
     * Serial version
     */
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long proyectoPeriodoJustificacionId;
    private TipoEstadoPeriodoJustificacion estado;
    private Instant fechaEstado;
    private String comentario;

  }
}
