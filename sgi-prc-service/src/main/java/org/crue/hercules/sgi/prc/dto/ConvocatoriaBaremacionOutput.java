package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

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
public class ConvocatoriaBaremacionOutput implements Serializable {
  private Long id;
  private String nombre;
  private Integer anio;
  private Integer aniosBaremables;
  private Integer ultimoAnio;
  private BigDecimal importeTotal;
  private String partidaPresupuestaria;
  private BigDecimal puntoProduccion;
  private BigDecimal puntoSexenio;
  private BigDecimal puntoCostesIndirectos;
  private Instant fechaInicioEjecucion;
  private Instant fechaFinEjecucion;
  private Boolean activo;
}
