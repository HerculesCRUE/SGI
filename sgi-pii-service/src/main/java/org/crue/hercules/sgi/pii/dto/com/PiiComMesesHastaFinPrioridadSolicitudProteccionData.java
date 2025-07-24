package org.crue.hercules.sgi.pii.dto.com;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;

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
  private Collection<? extends I18nFieldValue> solicitudTitle;
  private Integer monthsBeforeFechaFinPrioridad;
  private Instant fechaFinPrioridad;
}
