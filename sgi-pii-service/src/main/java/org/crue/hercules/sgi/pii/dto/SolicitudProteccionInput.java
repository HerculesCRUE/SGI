package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.SolicitudProteccion;

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
public class SolicitudProteccionInput {

  @NotNull
  private Long invencionId;

  @NotEmpty
  @Size(max = SolicitudProteccion.TITULO_MAX_LENGTH)
  private String titulo;

  @NotNull
  private Instant fechaPrioridadSolicitud;

  private Instant fechaFinPriorPresFasNacRec;

  private Instant fechaPublicacion;

  private Instant fechaConcesion;

  private Instant fechaCaducid;

  @NotNull
  private Long viaProteccionId;

  @NotEmpty
  @Size(max = SolicitudProteccion.NUMERO_SOLICITUD_MAX_LENGTH)
  private String numeroSolicitud;

  private String numeroPublicacion;

  private String numeroConcesion;

  private String numeroRegistro;

  private SolicitudProteccion.EstadoSolicitudProteccion estado;

  private Long tipoCaducidadId;

  private String agentePropiedadRef;

  private String paisProteccionRef;

  private String comentarios;
}
