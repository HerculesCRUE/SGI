package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.ConceptoGastoDescripcion;
import org.crue.hercules.sgi.csp.model.ConceptoGastoNombre;
import org.crue.hercules.sgi.csp.model.EstadoGastoProyectoComentario;
import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto.TipoEstadoGasto;
import org.crue.hercules.sgi.csp.model.GastoProyectoObservaciones;

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
public class GastoProyectoOutput implements Serializable {
  private Long id;
  private Long proyectoId;
  private String gastoRef;
  private ConceptoGasto conceptoGasto;
  private EstadoGastoProyecto estado;
  private Instant fechaCongreso;
  private BigDecimal importeInscripcion;
  private Collection<GastoProyectoObservaciones> observaciones;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ConceptoGasto implements Serializable {
    private Long id;
    private Collection<ConceptoGastoNombre> nombre;
    private Collection<ConceptoGastoDescripcion> descripcion;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EstadoGastoProyecto implements Serializable {
    private Long id;
    private TipoEstadoGasto estado;
    private Instant fechaEstado;
    private Collection<EstadoGastoProyectoComentario> comentario;
  }
}
