package org.crue.hercules.sgi.pii.dto.com;

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
public class PiiComMesesHastaFinPrioridadSolicitudProteccionData implements Serializable {
  private String solicitudTitle;
  private Integer monthsBeforeFechaFinPrioridad;
  private Instant fechaFinPrioridad;
}
