package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.prc.model.Rango.TipoRango;
import org.crue.hercules.sgi.prc.model.Rango.TipoTemporalidad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RangoInput implements Serializable {
  private Long id;
  @NotNull
  private TipoRango tipoRango;
  @NotNull
  private TipoTemporalidad tipoTemporalidad;
  private BigDecimal desde;
  private BigDecimal hasta;
  @NotNull
  private BigDecimal puntos;
  @NotNull
  private Long convocatoriaBaremacionId;
}
