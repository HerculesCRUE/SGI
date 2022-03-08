package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class NotificacionProyectoExternoCVNInput implements Serializable {

  @NotBlank
  @Size(max = 250)
  private String titulo;

  private Long autorizacionId;
  private Long proyectoId;
  private String ambitoGeografico;
  private String codExterno;
  private String datosEntidadParticipacion;
  private String datosResponsable;
  private String documentoRef;
  private String entidadParticipacionRef;

  @NotNull
  private Instant fechaInicio;

  @NotNull
  private Instant fechaFin;

  private String gradoContribucion;
  private Integer importeTotal;
  private String nombrePrograma;
  private Integer porcentajeSubvencion;

  @NotNull
  private String proyectoCVNId;
  private String responsableRef;

  @NotNull
  private String solicitanteRef;
  private String urlDocumentoAcreditacion;
  private List<NotificacionCVNEntidadFinanciadoraInput> notificacionesEntidadFinanciadoras;
}