package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrenciaNombre;

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
public class TipoRegimenConcurrenciaOutput implements Serializable {
  private Long id;
  private Collection<TipoRegimenConcurrenciaNombre> nombre;
  private Boolean activo;
}
