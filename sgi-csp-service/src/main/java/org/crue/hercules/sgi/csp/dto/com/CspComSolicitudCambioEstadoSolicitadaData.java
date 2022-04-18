package org.crue.hercules.sgi.csp.dto.com;

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
public class CspComSolicitudCambioEstadoSolicitadaData implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private String tituloConvocatoria;
  private String nombreApellidosSolicitante;
  private Instant fechaCambioEstadoSolicitud;
  private Instant fechaPublicacionConvocatoria;
}
