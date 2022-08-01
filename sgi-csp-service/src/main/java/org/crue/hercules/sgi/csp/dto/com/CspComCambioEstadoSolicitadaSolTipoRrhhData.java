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
public class CspComCambioEstadoSolicitadaSolTipoRrhhData implements Serializable {
  private Instant fechaEstado;
  private String nombreApellidosSolicitante;
  private String codigoInternoSolicitud;
  private String tituloConvocatoria;
  private Instant fechaProvisionalConvocatoria;
  private String enlaceAplicacionMenuValidacionTutor;

}
