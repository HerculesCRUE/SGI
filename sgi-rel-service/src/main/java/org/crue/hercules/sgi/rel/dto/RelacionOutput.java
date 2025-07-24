package org.crue.hercules.sgi.rel.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.rel.model.Relacion.TipoEntidad;
import org.crue.hercules.sgi.rel.model.RelacionObservaciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class RelacionOutput implements Serializable {
  private Long id;
  private TipoEntidad tipoEntidadOrigen;
  private TipoEntidad tipoEntidadDestino;
  private String entidadOrigenRef;
  private String entidadDestinoRef;
  private Collection<RelacionObservaciones> observaciones;
}
