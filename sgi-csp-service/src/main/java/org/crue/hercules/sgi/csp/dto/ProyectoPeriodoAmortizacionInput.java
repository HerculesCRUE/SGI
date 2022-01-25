package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.lang.NonNull;

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
public class ProyectoPeriodoAmortizacionInput implements Serializable {
  @NonNull
  private Instant fechaLimiteAmortizacion;
  @NonNull
  private Long importe;
  @NonNull
  private String proyectoSGERef;
  @NonNull
  private Long proyectoAnualidadId;
  @NonNull
  private Long proyectoEntidadFinanciadoraId;
}
