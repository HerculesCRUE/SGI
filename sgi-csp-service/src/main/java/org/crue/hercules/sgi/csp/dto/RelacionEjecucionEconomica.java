package org.crue.hercules.sgi.csp.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelacionEjecucionEconomica {

  public enum TipoEntidad {
    /** Grupo */
    GRUPO,
    /** Proyecto */
    PROYECTO
  }

  private Long id;
  private String nombre;
  private String codigoExterno;
  private String codigoInterno;
  private Instant fechaInicio;
  private Instant fechaFin;
  private String proyectoSgeRef;
  private TipoEntidad tipoEntidad;

  public RelacionEjecucionEconomica(Long id, String nombre, Instant fechaInicio, Instant fechaFin,
      String proyectoSgeRef, String tipoEntidad) {
    this(id, nombre, null, null, fechaInicio, fechaFin, proyectoSgeRef, tipoEntidad);
  }

  public RelacionEjecucionEconomica(Long id, String nombre, String codigoExterno,
      String codigoInterno, Instant fechaInicio, Instant fechaFin,
      String proyectoSgeRef, String tipoEntidad) {
    this.id = id;
    this.nombre = nombre;
    this.codigoExterno = codigoExterno;
    this.codigoInterno = codigoInterno;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.proyectoSgeRef = proyectoSgeRef;
    this.tipoEntidad = TipoEntidad.valueOf(tipoEntidad);
  }

}
