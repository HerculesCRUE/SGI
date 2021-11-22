package org.crue.hercules.sgi.csp.dto;

import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;

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
public class ProyectoFacturacionInput {

  public enum TipoEstadoValidacion {
    PENDIENTE, NOTIFICADA, VALIDADA, RECHAZADA
  }
  private Long id;

  @Size(max = ProyectoFacturacion.COMENTARIO_MAX_LENGTH)
  private String comentario;

  private Instant fechaConformidad;

  @NotNull
  private Instant fechaEmision;

  @NotNull
  private BigDecimal importeBase;

  @NotNull
  private Integer numeroPrevision;

  @NotNull
  private Integer porcentajeIVA;

  @NotNull
  private Long proyectoId;

  private EstadoValidacionIP estadoValidacionIP;

  private Long tipoFacturacionId;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EstadoValidacionIP {
    private Long id;
    private String comentario;
    @NotNull
    private TipoEstadoValidacion estado;
  }
}
