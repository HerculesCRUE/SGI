package org.crue.hercules.sgi.csp.dto;

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
public class NotificacionProyectoExternoCVNOutput implements Serializable {
  private Long id;
  private String titulo;
  private Long autorizacionId;
  private Long proyectoId;
  private String ambitoGeografico;
  private String codExterno;
  private String datosEntidadParticipacion;
  private String datosResponsable;
  private String documentoRef;
  private String entidadParticipacionRef;
  private Instant fechaInicio;
  private Instant fechaFin;
  private String gradoContribucion;
  private Integer importeTotal;
  private String nombrePrograma;
  private Integer porcentajeSubvencion;
  private String proyectoCVNId;
  private String responsableRef;
  private String solicitanteRef;
  private String urlDocumentoAcreditacion;
}
