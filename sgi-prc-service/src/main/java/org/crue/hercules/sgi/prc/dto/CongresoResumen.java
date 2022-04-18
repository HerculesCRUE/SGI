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
public class CongresoResumen extends ProduccionCientificaResumen {

  private Instant fechaCelebracion;
  private String tipoEvento;
  private String tituloTrabajo;

  public CongresoResumen(Long id, String produccionCientificaRef, TipoEstadoProduccion estado,
      EpigrafeCVN epigrafeCVN, String tipoEvento, String tituloTrabajo, String fechaCelebracion) {
    super(id, produccionCientificaRef, estado, epigrafeCVN);
    this.fechaCelebracion = StringUtils.hasText(fechaCelebracion) ? Instant.parse(fechaCelebracion) : null;
    this.tipoEvento = tipoEvento;
    this.tituloTrabajo = tituloTrabajo;
  }
}
