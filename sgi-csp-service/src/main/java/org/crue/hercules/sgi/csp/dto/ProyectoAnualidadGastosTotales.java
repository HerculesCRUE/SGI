package org.crue.hercules.sgi.csp.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoAnualidadGastosTotales {
  public static final String ALIAS_IMPORTE_CONCEDIDO_ANUALIDAD_COSTES_DIRECTOS = "importeConcendidoAnualidadCostesDirectos";
  public static final String ALIAS_IMPORTE_CONCEDIDO_ANUALIDAD_COSTES_INDIRECTOS = "importeConcendidoAnualidadCostesIndirectos";

  private BigDecimal importeConcendidoAnualidadCostesDirectos;
  private BigDecimal importeConcendidoAnualidadCostesIndirectos;
}
