package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoRanking;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class IndiceImpactoOutput implements Serializable {
  private Long id;
  private TipoRanking ranking;
  private Integer anio;
  private String otraFuenteImpacto;
  private BigDecimal indice;
  private BigDecimal posicionPublicacion;
  private BigDecimal numeroRevistas;
  private Boolean revista25;
  private Long produccionCientificaId;
  private String tipoFuenteImpacto;

  @JsonIgnore
  private TipoFuenteImpacto fuenteImpacto;

  public void setTipoFuenteImpacto(String tipoFuenteImpacto) {
    this.tipoFuenteImpacto = tipoFuenteImpacto;
    if (StringUtils.hasText(tipoFuenteImpacto)) {
      this.fuenteImpacto = TipoFuenteImpacto.getByInternValue(tipoFuenteImpacto);
    }
  }

  public void setFuenteImpacto(TipoFuenteImpacto fuenteImpacto) {
    if (null != fuenteImpacto) {
      this.tipoFuenteImpacto = fuenteImpacto.getInternValue();
    }
  }
}
