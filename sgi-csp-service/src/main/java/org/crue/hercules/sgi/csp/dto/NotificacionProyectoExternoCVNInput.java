package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;

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

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  @NotBlank
  private String titulo;

  private Long autorizacionId;

  private Long proyectoId;

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String ambitoGeografico;

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String codExterno;

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String datosEntidadParticipacion;

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String datosResponsable;

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String documentoRef;

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String entidadParticipacionRef;

  @NotNull
  private Instant fechaInicio;

  @NotNull
  private Instant fechaFin;

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String gradoContribucion;

  @Min(NotificacionProyectoExternoCVN.MIN_IMPORTE)
  private Integer importeTotal;

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String nombrePrograma;

  @Max(NotificacionProyectoExternoCVN.MAX_PERCENTAGE)
  @Min(NotificacionProyectoExternoCVN.MIN_PERCENTAGE)
  private Integer porcentajeSubvencion;

  @NotBlank
  private String proyectoCVNId;
  private String responsableRef;

  @NotBlank
  private String solicitanteRef;

  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String urlDocumentoAcreditacion;

  private List<NotificacionCVNEntidadFinanciadoraInput> notificacionesEntidadFinanciadoras;
}