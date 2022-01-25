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
public class AnualidadIngresoOutput implements Serializable {

  private Long id;

  @NotNull
  private Long proyectoAnualidadId;

  private String codigoEconomicoRef;

  private ProyectoPartida proyectoPartida;

  private BigDecimal importeConcedido;

  private String proyectoSgeRef;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProyectoPartida implements Serializable {
    private Long id;
    private String codigo;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ConceptGasto implements Serializable {
    private Long id;
    private String nombre;
  }
}
