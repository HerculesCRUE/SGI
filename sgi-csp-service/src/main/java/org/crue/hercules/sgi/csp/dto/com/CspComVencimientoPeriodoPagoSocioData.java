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
public class CspComVencimientoPeriodoPagoSocioData implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private String titulo;
  private Instant fechaPrevistaPago;
  private String nombreEntidadColaboradora;
}
