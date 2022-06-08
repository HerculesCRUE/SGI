package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.crue.hercules.sgi.prc.model.Rango.TipoRango;
import org.crue.hercules.sgi.prc.model.Rango.TipoTemporalidad;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class RangoOutput implements Serializable {
  private Long id;
  private TipoRango tipoRango;
  private TipoTemporalidad tipoTemporalidad;
  private BigDecimal desde;
  private BigDecimal hasta;
  private BigDecimal puntos;
  private Long convocatoriaBaremacionId;
}
