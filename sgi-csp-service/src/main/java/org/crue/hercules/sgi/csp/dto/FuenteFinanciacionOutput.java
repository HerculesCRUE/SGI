package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.FuenteFinanciacionDescripcion;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacionNombre;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacionNombre;

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
public class FuenteFinanciacionOutput implements Serializable {
  private Long id;
  private Collection<FuenteFinanciacionNombre> nombre;
  private Collection<FuenteFinanciacionDescripcion> descripcion;
  private Boolean fondoEstructural;
  private TipoAmbitoGeografico tipoAmbitoGeografico;
  private TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion;
  private Boolean activo;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoAmbitoGeografico implements Serializable {
    private Long id;
    private Collection<TipoAmbitoGeograficoNombre> nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoOrigenFuenteFinanciacion implements Serializable {
    private Long id;
    private Collection<TipoOrigenFuenteFinanciacionNombre> nombre;
  }
}
