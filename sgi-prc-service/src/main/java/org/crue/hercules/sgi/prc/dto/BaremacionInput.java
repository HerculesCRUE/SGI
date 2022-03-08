package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.crue.hercules.sgi.prc.model.Baremo.TipoCuantia;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoFuente;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoPuntos;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class BaremacionInput implements Serializable {

  private Integer anio;
  private String fechaInicio;
  private String fechaFin;
  private Long convocatoriaBaremacionId;
  private Long produccionCientificaId;
  private EpigrafeCVN epigrafeCVN;

  private BaremoInput baremo;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class BaremoInput implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer peso;
    private BigDecimal puntos;
    private BigDecimal cuantia;
    private TipoCuantia tipoCuantia;
    private ConfiguracionBaremoInput configuracionBaremo;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ConfiguracionBaremoInput implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private TipoBaremo tipoBaremo;
    private TipoFuente tipoFuente;
    private TipoPuntos tipoPuntos;
  }
}
