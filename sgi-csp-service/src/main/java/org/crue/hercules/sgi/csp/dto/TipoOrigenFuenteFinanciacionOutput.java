package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

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
public class TipoOrigenFuenteFinanciacionOutput implements Serializable {

  private Long id;
  private Collection<TipoOrigenFuenteFinanciacionNombre> nombre;
  private boolean activo;

}
