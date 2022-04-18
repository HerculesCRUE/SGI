package org.crue.hercules.sgi.csp.dto.com;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CspComPeriodoJustificacionSocioData implements Serializable {
  String titulo;
  String nombreEntidad;
  Instant fechaInicio;
  Instant fechaFin;
  Integer numPeriodo;
}
