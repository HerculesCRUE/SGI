package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

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
public class CspComSolicitudCambioEstadoDefinitivoData implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private Collection<? extends I18nFieldValue> tituloConvocatoria;
  private Instant fechaConcesionConvocatoria;
  private List<Enlace> enlaces;

}
