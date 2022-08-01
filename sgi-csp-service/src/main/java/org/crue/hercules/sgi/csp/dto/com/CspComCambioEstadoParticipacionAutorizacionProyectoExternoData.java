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
public class CspComCambioEstadoParticipacionAutorizacionProyectoExternoData implements Serializable {
  private String estadoSolicitudPext;
  private Instant fechaEstadoSolicitudPext;
  private String tituloPext;

}