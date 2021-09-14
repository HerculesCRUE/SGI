package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
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
public class SolicitudProteccionOutput implements Serializable {
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  private Long id;
  private Invencion invencion;
  private String titulo;
  private Instant fechaPrioridadSolicitud;
  private Instant fechaFinPriorPresFasNacRec;
  private Instant fechaPublicacion;
  private Instant fechaConcesion;
  private Instant fechaCaducid;
  private ViaProteccion viaProteccion;
  private String numeroSolicitud;
  private String numeroPublicacion;
  private String numeroConcesion;
  private String numeroRegistro;
  private SolicitudProteccion.EstadoSolicitudProteccion estado;
  private TipoCaducidad tipoCaducidad;
  private String agentePropiedadRef;
  private String paisProteccionRef;
  private String comentarios;
  private Boolean activo;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Invencion {
    Long id;
    Invencion.TipoProteccion tipoProteccion;

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TipoProteccion {
      Long id;
      TipoPropiedad tipoPropiedad;
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ViaProteccion {
    Long id;
    String nombre;
    TipoPropiedad tipoPropiedad;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoCaducidad {
    Long id;
    String descripcion;
  }
}
