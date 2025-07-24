package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.SolicitudRrhhObservaciones;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhResumen;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhTituloTrabajo;

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
public class SolicitudRrhhMemoriaOutput implements Serializable {
  private Collection<SolicitudRrhhTituloTrabajo> tituloTrabajo;
  private Collection<SolicitudRrhhResumen> resumen;
  private Collection<SolicitudRrhhObservaciones> observaciones;
}
