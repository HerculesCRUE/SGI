package org.crue.hercules.sgi.csp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoFacturacionOutput {

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

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EstadoValidacionIP {
    private Long id;
    private TipoEstadoValidacion estado;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoFacturacion {
    Long id;
    String nombre;
  }

}
