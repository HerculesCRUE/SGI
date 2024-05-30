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
public class ResumenPuntuacionGrupoAnioOutput {

  private Integer anio;
  private List<ResumenPuntuacionGrupo> puntuacionesGrupos;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ResumenPuntuacionGrupo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String grupo;
    private String personaResponsable;
    private BigDecimal puntosSexenios;
    private BigDecimal puntosCostesIndirectos;
    private BigDecimal puntosProduccion;
  }

}
