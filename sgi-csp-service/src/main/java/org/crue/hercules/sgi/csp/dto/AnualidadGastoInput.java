package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

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
public class AnualidadGastoInput implements Serializable {
  private Long id;

  @NotNull
  private Long anualidadId;

  private ConceptoGasto conceptoGasto;

  private String codigoEconomicoRef;

  private ProyectoPartida proyectoPartida;

  private BigDecimal importePresupuesto;

  private BigDecimal importeConcedido;

  private String proyectoSgeRef;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ConceptoGasto {
    private Long id;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProyectoPartida {
    private Long id;
  }
}
