package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.AutorizacionDatosConvocatoria;
import org.crue.hercules.sgi.csp.model.AutorizacionObservaciones;
import org.crue.hercules.sgi.csp.model.AutorizacionTituloProyecto;

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
public class AutorizacionWithFirstEstado implements Serializable {
  private Long id;
  private Collection<AutorizacionObservaciones> observaciones;
  private String responsableRef;
  private String solicitanteRef;
  private Collection<AutorizacionTituloProyecto> tituloProyecto;
  private String entidadRef;
  private Integer horasDedicacion;
  private String datosResponsable;
  private String datosEntidad;
  private Collection<AutorizacionDatosConvocatoria> datosConvocatoria;
  private Long convocatoriaId;
  private Long estadoId;
  private Instant fechaFirstEstado;
}
