package org.crue.hercules.sgi.prc.dto.rel;

import java.io.Serializable;

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
  public enum TipoEntidad {
    /** Proyecto */
    PROYECTO,
    /** Convocatoria */
    CONVOCATORIA,
    /** Invencion */
    INVENCION
  }

  private Long id;
  private TipoEntidad tipoEntidadOrigen;
  private TipoEntidad tipoEntidadDestino;
  private String entidadOrigenRef;
  private String entidadDestinoRef;
  private String observaciones;
}
