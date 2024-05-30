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
public class DetalleProduccionInvestigadorOutput {

  private String investigador;
  private Integer anio;
  private List<TipoProduccionInvestigadorOutput> tipos;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoProduccionInvestigadorOutput implements Serializable {
    private static final long serialVersionUID = 1L;
    private String titulo;
    private BigDecimal puntos;
    private List<TipoProduccionInvestigadorOutput> produccionesCientificas;
  }

}
