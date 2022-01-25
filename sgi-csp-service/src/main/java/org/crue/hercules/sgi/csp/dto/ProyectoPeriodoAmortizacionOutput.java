package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

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
public class ProyectoPeriodoAmortizacionOutput implements Serializable {
  private Long Id;
  private Instant fechaLimiteAmortizacion;
  private Long importe;
  private String proyectoSGERef;
  private Long proyectoAnualidadId;
  private Long proyectoEntidadFinanciadoraId;

}
