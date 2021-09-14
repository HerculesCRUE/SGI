package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto.TipoEstadoGasto;

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
  private String observaciones;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ConceptoGasto {
    private Long id;
    private String nombre;
    private String descripcion;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EstadoGastoProyecto {
    private Long id;
    private TipoEstadoGasto estado;
    private Instant fechaEstado;
    private String comentario;
  }
}
