package org.crue.hercules.sgi.rep.dto.prc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
public class DetalleGrupoInvestigacionOutput {

  private String grupo;
  private Integer anio;
  private List<ResumenInvestigadorOutput> investigadores;
  private BigDecimal precioPuntoProduccion;
  private BigDecimal precioPuntoCostesIndirectos;
  private BigDecimal precioPuntoSexenio;

  private ResumenSexenioOutput sexenios;
  private List<ResumenProduccionCientificaOutput> produccionesCientificas;
  private ResumenCosteIndirectoOutput costesIndirectos;
  private List<ResumenTotalOutput> totales;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ResumenInvestigadorOutput implements Serializable {
    private static final long serialVersionUID = 1L;
    private String investigador;
    private BigDecimal puntosProduccion;
    private BigDecimal puntosCostesIndirectos;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ResumenSexenioOutput implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer numero;
    private BigDecimal puntos;
    private BigDecimal importe;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ResumenProduccionCientificaOutput implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tipo;
    private Integer numero;
    private BigDecimal puntos;
    private BigDecimal importe;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ResumenCosteIndirectoOutput implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer numero;
    private BigDecimal puntos;
    private BigDecimal importe;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ResumenTotalOutput implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tipo;
    private BigDecimal importe;
  }
}
