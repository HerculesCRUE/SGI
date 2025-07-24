package org.crue.hercules.sgi.csp.dto.com;

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
public class CspComCambioEstadoValidadaSolTipoRrhhData implements Serializable {
  private Instant fechaEstado;
  private String nombreApellidosSolicitante;
  private String codigoInternoSolicitud;
  private Collection<? extends I18nFieldValue> tituloConvocatoria;
}
