package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.model.InvencionTitulo;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccionComentarios;
import org.crue.hercules.sgi.pii.model.SolicitudProteccionTitulo;
import org.crue.hercules.sgi.pii.model.TipoCaducidadDescripcion;
import org.crue.hercules.sgi.pii.model.TipoProteccionNombre;
import org.crue.hercules.sgi.pii.model.ViaProteccionDescripcion;
import org.crue.hercules.sgi.pii.model.ViaProteccionNombre;

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
public class SolicitudProteccionOutput implements Serializable {
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  private Long id;
  private Invencion invencion;
  private Collection<SolicitudProteccionTitulo> titulo;
  private Instant fechaPrioridadSolicitud;
  private Instant fechaFinPriorPresFasNacRec;
  private Instant fechaPublicacion;
  private Instant fechaConcesion;
  private Instant fechaCaducidad;
  private ViaProteccion viaProteccion;
  private String numeroSolicitud;
  private String numeroPublicacion;
  private String numeroConcesion;
  private String numeroRegistro;
  private SolicitudProteccion.EstadoSolicitudProteccion estado;
  private TipoCaducidad tipoCaducidad;
  private String agentePropiedadRef;
  private String paisProteccionRef;
  private Collection<SolicitudProteccionComentarios> comentarios;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Invencion implements Serializable {
    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private Long id;
    private Invencion.TipoProteccion tipoProteccion;
    private Collection<InvencionTitulo> titulo;

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TipoProteccion implements Serializable {
      /**
      *
      */
      private static final long serialVersionUID = 1L;

      private Long id;
      private TipoPropiedad tipoPropiedad;
      private Collection<TipoProteccionNombre> nombre;
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ViaProteccion implements Serializable {
    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private Long id;
    private Collection<ViaProteccionNombre> nombre;
    private Collection<ViaProteccionDescripcion> descripcion;
    private TipoPropiedad tipoPropiedad;
    private Boolean paisEspecifico;
    private Integer mesesPrioridad;
    private Boolean extensionInternacional;
    private Boolean variosPaises;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoCaducidad implements Serializable {
    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private Long id;
    private Collection<TipoCaducidadDescripcion> descripcion;
  }
}
