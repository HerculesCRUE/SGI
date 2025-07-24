package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

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
public class ProyectoFacturacionInput implements Serializable {

  public enum TipoEstadoValidacion {
    PENDIENTE, NOTIFICADA, VALIDADA, RECHAZADA
  }

  private Long id;

  private List<I18nFieldValueDto> comentario;

  private Instant fechaConformidad;

  @NotNull
  private Instant fechaEmision;

  @NotNull
  private BigDecimal importeBase;

  private String numeroFacturaSge;

  @NotNull
  private Integer numeroPrevision;

  @NotNull
  private Integer porcentajeIVA;

  @NotNull
  private Long proyectoId;

  private EstadoValidacionIP estadoValidacionIP;

  private Long tipoFacturacionId;

  private Long proyectoProrrogaId;

  private String proyectoSgeRef;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EstadoValidacionIP implements Serializable {
    private Long id;
    private List<I18nFieldValueDto> comentario;
    @NotNull
    private TipoEstadoValidacion estado;
  }
}
