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
public class ActividadResumen extends ProduccionCientificaResumen {

  private Instant fechaInicio;
  private String tituloActividad;

  public ActividadResumen(Long id, String produccionCientificaRef, TipoEstadoProduccion estado,
      EpigrafeCVN epigrafeCVN, String tituloActividad, String fechaInicio) {
    super(id, produccionCientificaRef, estado, epigrafeCVN);
    this.tituloActividad = tituloActividad;
    this.fechaInicio = StringUtils.hasText(fechaInicio) ? Instant.parse(fechaInicio) : null;
  }
}
