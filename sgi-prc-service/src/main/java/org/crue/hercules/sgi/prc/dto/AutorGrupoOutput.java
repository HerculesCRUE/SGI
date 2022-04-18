package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AutorGrupoOutput implements Serializable {
  private Long id;
  private TipoEstadoProduccion estado;
  private Long grupoRef;
  private Long autorId;
}
