package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.ProyectoFaseObservaciones;
import org.crue.hercules.sgi.csp.model.TipoFaseDescripcion;
import org.crue.hercules.sgi.csp.model.TipoFaseNombre;

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
public class ProyectoFaseOutput implements Serializable {
  private Long id;
  private Long proyectoId;
  private TipoFase tipoFase;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Collection<ProyectoFaseObservaciones> observaciones;
  private ProyectoFaseAvisoOutput aviso1;
  private ProyectoFaseAvisoOutput aviso2;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoFase implements Serializable {
    private Long id;
    private Collection<TipoFaseNombre> nombre;
    private Collection<TipoFaseDescripcion> descripcion;
    private Boolean activo;
  }
}
