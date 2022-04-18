package org.crue.hercules.sgi.prc.dto;

import java.time.Instant;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class DireccionTesisResumen extends ProduccionCientificaResumen {

  private Instant fechaDefensa;
  private String tituloTrabajo;

  public DireccionTesisResumen(Long id, String produccionCientificaRef, TipoEstadoProduccion estado,
      EpigrafeCVN epigrafeCVN, String tituloTrabajo, String fechaDefensa) {
    super(id, produccionCientificaRef, estado, epigrafeCVN);
    this.tituloTrabajo = tituloTrabajo;
    this.fechaDefensa = StringUtils.hasText(fechaDefensa) ? Instant.parse(fechaDefensa) : null;
  }
}
