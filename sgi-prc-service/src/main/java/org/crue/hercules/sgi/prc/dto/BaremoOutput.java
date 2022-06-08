package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.crue.hercules.sgi.prc.model.Baremo.TipoCuantia;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BaremoOutput implements Serializable {
  private Long id;
  private Integer peso;
  private BigDecimal puntos;
  private BigDecimal cuantia;
  private TipoCuantia tipoCuantia;
  private Long configuracionBaremoId;
  private Long convocatoriaBaremacionId;
}
