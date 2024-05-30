package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

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
public class ProyectoFacturacionOutput implements Serializable {

  public enum TipoEstadoValidacion {
    PENDIENTE, NOTIFICADA, VALIDADA, RECHAZADA
  }

  private Long id;
  private String comentario;
  private Instant fechaConformidad;
  private Instant fechaEmision;
  private BigDecimal importeBase;
  private Integer numeroPrevision;
  private Integer porcentajeIVA;
  private Long proyectoId;
  private EstadoValidacionIP estadoValidacionIP;
  private TipoFacturacion tipoFacturacion;
  private Long proyectoProrrogaId;
  private String proyectoSgeRef;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EstadoValidacionIP implements Serializable {
    private Long id;
    private TipoEstadoValidacion estado;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoFacturacion implements Serializable {
    Long id;
    String nombre;
  }

}
