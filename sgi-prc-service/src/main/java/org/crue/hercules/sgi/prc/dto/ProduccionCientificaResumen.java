package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProduccionCientificaResumen implements Serializable {
  private Long id;
  private String produccionCientificaRef;
  private EpigrafeCVN epigrafeCVN;
  private TipoEstadoProduccion estado;

  public ProduccionCientificaResumen(Long id, String produccionCientificaRef, TipoEstadoProduccion estado,
      EpigrafeCVN epigrafeCVN) {
    this.id = id;
    this.produccionCientificaRef = produccionCientificaRef;
    this.estado = estado;
    this.epigrafeCVN = epigrafeCVN;
  }

}
